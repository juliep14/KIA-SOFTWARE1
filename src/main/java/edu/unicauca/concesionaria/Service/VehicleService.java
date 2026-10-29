package edu.unicauca.concesionaria.Service;
 
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Error.excValidationException;
import edu.unicauca.concesionaria.Model.clsVehicle;
import edu.unicauca.concesionaria.Repository.VehicleRepository;
import org.springframework.stereotype.Service;
 
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
 
@Service
public class VehicleService {
 
    private static final List<String> FUEL_TYPES = List.of("electric", "gasoline", "hybrid");
    private static final List<String> CATEGORIES = List.of("standard", "luxury");
    private static final List<String> CONDITIONS = List.of("used", "new");
    private static final List<String> STATUSES = List.of("available", "reserved", "sold", "maintenance");
 
    private static final String DEFAULT_STATUS = "available";
    private static final String PLATE_PATTERN = "^[A-Z0-9]{6}$";
    private static final String COLOR_PATTERN = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$";
 
    private final VehicleRepository attVehicleRepository;
 
    public VehicleService(VehicleRepository prmVehicleRepository) {
        this.attVehicleRepository = prmVehicleRepository;
    }
 
    // ---------- Helpers ----------
 
    private boolean opIsBlank(String prmValue) {
        return prmValue == null || prmValue.isBlank();
    }
 
    private String opNormalizePlate(String prmPlate) {
        String varPlate = prmPlate.trim().toUpperCase();
        if (!varPlate.matches(PLATE_PATTERN)) {
            throw new excValidationException("Error: Formato de placa inválido, debe tener exactamente 6 caracteres (letras y números)");
        }
        return varPlate;
    }
 
    private String opNormalizeChoice(String prmValue, List<String> prmAllowed, String prmFieldName) {
        String varValue = prmValue.trim().toLowerCase();
        if (!prmAllowed.contains(varValue)) {
            throw new excValidationException("Error: " + prmFieldName + " inválido. Valores permitidos: "
                    + String.join(", ", prmAllowed));
        }
        return varValue;
    }
 
    private String opNormalizeText(String prmValue, String prmFieldName, int prmMax) {
        String varValue = prmValue.trim();
        if (varValue.length() < 3 || varValue.length() > prmMax) {
            throw new excValidationException("Error: " + prmFieldName + " debe tener entre 3 y " + prmMax + " caracteres");
        }
        return varValue;
    }
 
    private String opNormalizeColor(String prmColor) {
        String varColor = opNormalizeText(prmColor, "El color", 20);
        if (!varColor.matches(COLOR_PATTERN)) {
            throw new excValidationException("Error: caracteres no permitidos en el color");
        }
        return varColor;
    }
 
    private void opValidateYear(Integer prmYear) {
        if (prmYear != null && (prmYear < 1990 || prmYear > Year.now().getValue() + 1)) {
            throw new excValidationException("Error: Año fuera del rango permitido");
        }
    }
 
    private void opValidateMileage(Double prmMileage) {
        if (prmMileage != null) {
            clsValidations.opValidatePositiveNumber(prmMileage, "Kilometraje");
        }
    }
 
    private clsVehicle opFetchVehicle(int prmId) {
        clsVehicle varVehicle = attVehicleRepository.opGetVehicleById(prmId);
        if (varVehicle == null) {
            throw new excNotFoundException("Vehículo no encontrado");
        }
        return varVehicle;
    }
 
    // ---------- HU-02: Registrar unidad ----------
 
    public void opRegisterVehicle(Integer prmId, String prmModel, Integer prmYear, String prmBodyType,
                                  String prmFuelType, String prmCategory, String prmLicensePlate,
                                  Double prmMileage, String prmCondition, String prmColor, String prmStatus) {
 
        // Escenario 2: datos obligatorios incompletos (se informan los campos faltantes)
        List<String> varMissing = new ArrayList<>();
        if (prmId == null) varMissing.add("id");
        if (opIsBlank(prmModel)) varMissing.add("modelo");
        if (opIsBlank(prmBodyType)) varMissing.add("tipo de carrocería");
        if (opIsBlank(prmFuelType)) varMissing.add("tipo de combustible");
        if (opIsBlank(prmCategory)) varMissing.add("categoría");
        if (opIsBlank(prmLicensePlate)) varMissing.add("placa");
        if (opIsBlank(prmCondition)) varMissing.add("condición");
        if (opIsBlank(prmColor)) varMissing.add("color");
        if (!varMissing.isEmpty()) {
            throw new excValidationException("Error: Datos obligatorios incompletos: " + String.join(", ", varMissing));
        }
 
        clsValidations.opValidateId(prmId.intValue());
        String varModel = opNormalizeText(prmModel, "El modelo", 25);
        opValidateYear(prmYear);
        String varBodyType = opNormalizeText(prmBodyType, "La carrocería", 25);
        String varFuel = opNormalizeChoice(prmFuelType, FUEL_TYPES, "Tipo de combustible");
        String varCategory = opNormalizeChoice(prmCategory, CATEGORIES, "Categoría");
        String varPlate = opNormalizePlate(prmLicensePlate);
        opValidateMileage(prmMileage);
        String varCondition = opNormalizeChoice(prmCondition, CONDITIONS, "Condición");
        String varColor = opNormalizeColor(prmColor);
        String varStatus = opIsBlank(prmStatus) ? DEFAULT_STATUS : opNormalizeChoice(prmStatus, STATUSES, "Estado");
 
        if (attVehicleRepository.opVehicleExist(prmId)) {
            throw new excDuplicateDataException("La unidad ya existe con ese id");
        }
        // Escenario 3: placa duplicada
        if (attVehicleRepository.opPlateExist(varPlate)) {
            throw new excDuplicateDataException("La placa ya está en uso");
        }
 
        clsVehicle varVehicle = new clsVehicle();
        varVehicle.setAttVehicleId(prmId);
        varVehicle.setAttModel(varModel);
        varVehicle.setAttYear(prmYear);
        varVehicle.setAttBodyType(varBodyType);
        varVehicle.setAttFuelType(varFuel);
        varVehicle.setAttCategory(varCategory);
        varVehicle.setAttLicensePlate(varPlate);
        varVehicle.setAttMileage(prmMileage);
        varVehicle.setAttCondition(varCondition);
        varVehicle.setAttStatus(varStatus);
        varVehicle.setAttColor(varColor);
 
        // Escenario 1: registro exitoso (la unidad entra al inventario)
        attVehicleRepository.opRegisterVehicle(varVehicle);
    }
 
    // ---------- HU-03: Modificar unidad ----------
 
    /**
     * Los campos nulos o vacíos se dejan como estaban; solo se cambian los que llegan con valor.
     */
    public void opUpdateVehicle(int prmId, String prmModel, Integer prmYear, String prmBodyType,
                                String prmFuelType, String prmCategory, String prmLicensePlate,
                                Double prmMileage, String prmCondition, String prmColor, String prmStatus) {
 
        clsValidations.opValidateId(prmId);
 
        // Escenario 2: unidad no encontrada
        clsVehicle varVehicle = opFetchVehicle(prmId);
 
        if (!opIsBlank(prmModel)) {
            varVehicle.setAttModel(opNormalizeText(prmModel, "El modelo", 25));
        }
        if (prmYear != null) {
            opValidateYear(prmYear);
            varVehicle.setAttYear(prmYear);
        }
        if (!opIsBlank(prmBodyType)) {
            varVehicle.setAttBodyType(opNormalizeText(prmBodyType, "La carrocería", 25));
        }
        if (!opIsBlank(prmFuelType)) {
            varVehicle.setAttFuelType(opNormalizeChoice(prmFuelType, FUEL_TYPES, "Tipo de combustible"));
        }
        if (!opIsBlank(prmCategory)) {
            varVehicle.setAttCategory(opNormalizeChoice(prmCategory, CATEGORIES, "Categoría"));
        }
        if (!opIsBlank(prmLicensePlate)) {
            String varPlate = opNormalizePlate(prmLicensePlate);
            if (attVehicleRepository.opPlateExistInOtherVehicle(varPlate, prmId)) {
                throw new excDuplicateDataException("La placa ya está en uso");
            }
            varVehicle.setAttLicensePlate(varPlate);
        }
        if (prmMileage != null) {
            opValidateMileage(prmMileage);
            varVehicle.setAttMileage(prmMileage);
        }
        if (!opIsBlank(prmCondition)) {
            varVehicle.setAttCondition(opNormalizeChoice(prmCondition, CONDITIONS, "Condición"));
        }
        if (!opIsBlank(prmColor)) {
            varVehicle.setAttColor(opNormalizeColor(prmColor));
        }
        if (!opIsBlank(prmStatus)) {
            varVehicle.setAttStatus(opNormalizeChoice(prmStatus, STATUSES, "Estado"));
        }
 
        // Escenario 1: actualización exitosa
        attVehicleRepository.opUpdateVehicle(varVehicle);
    }
 
    // Escenario 3: cambio de estado (por ejemplo, de "available" a "maintenance")
    public void opChangeVehicleStatus(int prmId, String prmStatus) {
        clsValidations.opValidateId(prmId);
        if (opIsBlank(prmStatus)) {
            throw new excValidationException("Error: Datos obligatorios incompletos: estado");
        }
        opFetchVehicle(prmId);
        attVehicleRepository.opUpdateVehicleStatus(prmId, opNormalizeChoice(prmStatus, STATUSES, "Estado"));
    }
 
    // ---------- Consultas (las necesita la interfaz) ----------
 
    public clsVehicle opGetVehicleById(int prmId) {
        clsValidations.opValidateId(prmId);
        return opFetchVehicle(prmId);
    }
 
    public List<clsVehicle> opGetAllVehicles() {
        return attVehicleRepository.opGetAllVehicles();
    }
}