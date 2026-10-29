package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excValidationException;

import java.sql.Date;

public class clsValidations {

    private clsValidations() {
    }

    public static void opValidatePhone(String prmPhone) throws excValidationException {
        if (prmPhone == null || !prmPhone.matches("^\\d{10}$"))
            throw new excValidationException("Error: Teléfono debe tener exactamente 10 dígitos");
    }

    public static void opValidateEmail(String prmEmail) throws excValidationException {
        if (prmEmail != null)
            if (!prmEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") || prmEmail.length() > 50)
                throw new excValidationException("Error: Email inválido o mayor a 50 caracteres");
    }

    public static void opValidateState(String prmState) throws excValidationException {
        if (prmState == null || prmState.isBlank())
            throw new excValidationException("Error: Estado vacío o nulo");
        if (!prmState.toLowerCase().trim().equals("active") && !prmState.toLowerCase().trim().equals("inactive"))
            throw new excValidationException("Error: Estado no permitido");
    }

    public static void opValidateDate(Date prmDate) throws excValidationException {
        if (prmDate == null)
            throw new excValidationException("Error: Fecha nula");
        long varToday = System.currentTimeMillis() / 86400000L * 86400000L;
        if (prmDate.getTime() > varToday + 86400000L)
            throw new excValidationException("Error: Fecha futura no permitida");
    }

    public static void opValidateName(String prmName, int prmMin, int prmMax) throws excValidationException {
        if (prmName == null || prmName.isBlank())
            throw new excValidationException("Error: Nombre vacío o nulo");
        if (!prmName.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$"))
            throw new excValidationException("Error: caracteres no permitidos en el nombre");
        if (prmName.length() < prmMin || prmName.length() > prmMax)
            throw new excValidationException("Error: Nombre fuera del rango");
    }

    public static void opValidatePositiveNumber(double prmValue, String prmField) throws excValidationException {
        if (prmValue < 0)
            throw new excValidationException("Error: " + prmField + " debe ser mayor a cero");
    }

    public static void opValidateSalary(double prmSalary) throws excValidationException {
        opValidatePositiveNumber(prmSalary, "Salario");
        if (prmSalary > 99_999_999)
            throw new excValidationException("Error: Salario excede NUMBER(8)");
    }

    public static void opValidateRole(String prmRole) throws excValidationException {
        if (prmRole == null || prmRole.isBlank())
            throw new excValidationException("Error: Rol vacío o nulo");
        String varRole = prmRole.toLowerCase().trim();
        if (!varRole.equals("seller") && !varRole.equals("manager"))
            throw new excValidationException("Error: Rol no permitido (manager/seller)");
    }

    public static void opValidateId(int prmId) throws excValidationException {
        if (prmId <= 0)
            throw new excValidationException("Error: Id debe ser mayor a cero");
    }

    public static void opValidateId(Integer prmId) throws excValidationException {
        if (prmId != null && prmId <= 0)
            throw new excValidationException("Error: Id debe ser mayor a cero");
    }

    public static void opValidateGoalType(String prmGoalType) throws excValidationException {
        if (prmGoalType == null || prmGoalType.isBlank())
            throw new excValidationException("Error: Tipo de meta vacío o nulo");
        String varType = prmGoalType.toLowerCase().trim();
        if (!varType.equals("monthly") && !varType.equals("quarterly") && !varType.equals("yearly"))
            throw new excValidationException("Error: Tipo de meta inválido");
    }

    public static void opValidateSalesGoalState(String prmState) throws excValidationException {
        if (prmState == null || prmState.isBlank())
            throw new excValidationException("Error: Estado de meta vacío o nulo");
        String varState = prmState.toLowerCase().trim();
        if (!varState.equals("inactive") && !varState.equals("active") && !varState.equals("complete"))
            throw new excValidationException("Error: Estado de meta inválido");
    }

    public static void opValidateTargetValue(double prmValue) throws excValidationException {
        opValidatePositiveNumber(prmValue, "El valor de la meta");
        if (prmValue <= 0)
            throw new excValidationException("Error: El valor de la meta debe ser mayor a cero");
        if (prmValue > 999_999_999_999.999)
            throw new excValidationException("Error: El valor de la meta excede el límite permitido");
    }

    public static void opValidateDateRange(Date prmStartDate, Date prmEndDate) throws excValidationException {
        if (prmEndDate != null && prmStartDate != null && prmEndDate.before(prmStartDate))
            throw new excValidationException("Error: La fecha final no puede ser menor a la inicial");
    }
}
