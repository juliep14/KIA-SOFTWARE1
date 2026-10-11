package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Error.excValidationException;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
import edu.unicauca.concesionaria.Repository.SalesGoalRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
public class SalesGoalService {

    private final SalesGoalRepository attSalesGoalRepository;
    private final EmployeeService attEmployeeService;
    private HashMap<Integer, clsSalesGoal> attSalesGoals = new HashMap<>();

    public SalesGoalService(SalesGoalRepository prmSalesGoalRepository, EmployeeService prmEmployeeService) {
        this.attSalesGoalRepository = prmSalesGoalRepository;
        this.attEmployeeService = prmEmployeeService;
    }

    private clsSalesGoal opFetchFromDB(int prmId) {
        clsSalesGoal varSalesGoal;
        try {
            varSalesGoal = attSalesGoalRepository.opGetSalesGoalById(prmId);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener la meta de venta desde BD: ", e);
        }
        if (varSalesGoal == null) {
            throw new excNotFoundException("Meta de venta no encontrada");
        }

        if (varSalesGoal.getAttEmployee() != null) {
            try {
                clsEmployee varEmployee = attEmployeeService.opGetEmployeeById(varSalesGoal.getAttEmployee().getAttEmployeeId());
                varSalesGoal.setAttEmployee(varEmployee);
            } catch (Exception ignored) {
            }
        }
        opPopulateCompliance(varSalesGoal);
        return varSalesGoal;
    }

    public void opPopulateCompliance(clsSalesGoal prmGoal) {
        if (prmGoal == null || prmGoal.getAttEmployee() == null) {
            return;
        }
        double varAchieved = attSalesGoalRepository.opGetAchievedSales(
                prmGoal.getAttEmployee().getAttEmployeeId(),
                prmGoal.getAttStartDate(),
                prmGoal.getAttEndDate()
        );
        prmGoal.setAttAchievedValue(varAchieved);
        if (prmGoal.getAttTargetValue() > 0) {
            double varPct = (varAchieved / prmGoal.getAttTargetValue()) * 100.0;
            prmGoal.setAttCompliancePercentage(Math.round(varPct * 100.0) / 100.0);
        } else {
            prmGoal.setAttCompliancePercentage(0.0);
        }
    }

    private void opRefreshCache(int prmId) {
        attSalesGoals.remove(prmId);
        clsSalesGoal varFresh = opFetchFromDB(prmId);
        attSalesGoals.put(prmId, varFresh);
    }

    public static Date opCalculateEndDate(Date prmStartDate, String prmGoalType) {
        if (prmStartDate == null || prmGoalType == null) {
            return null;
        }
        LocalDate varStart = prmStartDate.toLocalDate();
        String varType = prmGoalType.toLowerCase().trim();
        LocalDate varEnd = switch (varType) {
            case "monthly" -> varStart.plusDays(30);
            case "quarterly" -> varStart.plusDays(90);
            case "yearly" -> varStart.plusDays(365);
            default -> varStart.plusDays(30);
        };
        return Date.valueOf(varEnd);
    }

    public int opRegisterSalesGoal(Integer prmSalesGoalId, int prmEmployeeId,
                                   String prmGoalType, double prmTargetValue, Date prmStartDate, String prmGoalState) {

        if (prmSalesGoalId != null && prmSalesGoalId > 0) {
            clsValidations.opValidateId(prmSalesGoalId);
            if (attSalesGoalRepository.opSalesGoalExist(prmSalesGoalId)) {
                throw new excDuplicateDataException("Meta de venta ya registrada con ID: " + prmSalesGoalId);
            }
        }

        clsValidations.opValidateId(prmEmployeeId);
        clsEmployee varEmployee = attEmployeeService.opGetEmployeeById(prmEmployeeId);

        if ("inactive".equalsIgnoreCase(varEmployee.getAttState())) {
            throw new excValidationException("No se puede asignar una meta a un empleado inactivo");
        }

        if (attSalesGoalRepository.opHasActiveGoal(prmEmployeeId)) {
            throw new excValidationException("No se pueden asignar 2 metas activas al mismo empleado");
        }

        clsValidations.opValidateGoalType(prmGoalType);
        clsValidations.opValidateTargetValue(prmTargetValue);
        clsValidations.opValidateSalesGoalState(prmGoalState);

        if (prmStartDate == null) {
            throw new excValidationException("Error: Fecha de inicio nula");
        }

        Date varEndDate = opCalculateEndDate(prmStartDate, prmGoalType);

        clsSalesGoal varTemp = new clsSalesGoal();
        if (prmSalesGoalId != null && prmSalesGoalId > 0) {
            varTemp.setAttSalesGoalId(prmSalesGoalId);
        }
        varTemp.setAttEmployee(varEmployee);
        varTemp.setAttGoalType(prmGoalType.toLowerCase().trim());
        varTemp.setAttTargetValue(prmTargetValue);
        varTemp.setAttStartDate(prmStartDate);
        varTemp.setAttEndDate(varEndDate);
        varTemp.setAttState(prmGoalState);

        int varId;
        try {
            varId = attSalesGoalRepository.opRegisterSalesGoal(varTemp);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al registrar la meta de venta: ", e);
        }

        opRefreshCache(varId);
        return varId;
    }

    public void opUpdateSalesGoal(int prmId, Double prmTargetValue, String prmGoalType) {
        clsValidations.opValidateId(prmId);

        if (!attSalesGoalRepository.opSalesGoalExist(prmId)) {
            throw new excNotFoundException("Meta de venta no encontrada");
        }

        clsSalesGoal varGoal = opFetchFromDB(prmId);

        if ("complete".equalsIgnoreCase(varGoal.getAttState())) {
            throw new excValidationException("No se puede modificar una meta ya completada");
        }

        if (prmTargetValue != null) {
            clsValidations.opValidateTargetValue(prmTargetValue);
            varGoal.setAttTargetValue(prmTargetValue);
        }

        if (prmGoalType != null && !prmGoalType.isBlank()) {
            clsValidations.opValidateGoalType(prmGoalType);
            varGoal.setAttGoalType(prmGoalType.toLowerCase().trim());
            Date varStartDate = varGoal.getAttStartDate() instanceof Date d
                    ? d
                    : new Date(varGoal.getAttStartDate().getTime());
            varGoal.setAttEndDate(opCalculateEndDate(varStartDate, varGoal.getAttGoalType()));
        }

        try {
            attSalesGoalRepository.opUpdateSalesGoal(varGoal);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al actualizar la meta de venta: ", e);
        }

        opRefreshCache(prmId);
    }

    public void opDisableSalesGoal(int prmId) {
        clsValidations.opValidateId(prmId);

        if (!attSalesGoalRepository.opSalesGoalExist(prmId)) {
            throw new excNotFoundException("Meta de venta no encontrada");
        }

        clsSalesGoal varGoal = opFetchFromDB(prmId);

        if ("complete".equalsIgnoreCase(varGoal.getAttState())) {
            throw new excValidationException("La meta ya fue completada, no se puede inactivar");
        }

        try {
            attSalesGoalRepository.opDisableSalesGoal(prmId);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al inactivar la meta de venta: ", e);
        }

        opRefreshCache(prmId);
    }

    public void opCompleteSalesGoal(int prmId) {
        clsValidations.opValidateId(prmId);

        if (!attSalesGoalRepository.opSalesGoalExist(prmId)) {
            throw new excNotFoundException("Meta de venta no encontrada");
        }

        clsSalesGoal varGoal = opFetchFromDB(prmId);

        if ("inactive".equalsIgnoreCase(varGoal.getAttState()) || "complete".equalsIgnoreCase(varGoal.getAttState())) {
            throw new excValidationException("Meta ya terminada o inactivada");
        }

        try {
            attSalesGoalRepository.opCompleteSalesGoal(prmId);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al completar la meta de venta: ", e);
        }

        opRefreshCache(prmId);
    }

    public clsSalesGoal opGetSalesGoalById(int prmId) {
        clsValidations.opValidateId(prmId);

        clsSalesGoal varGoal = attSalesGoals.get(prmId);
        if (varGoal != null) {
            return varGoal;
        }

        varGoal = opFetchFromDB(prmId);
        attSalesGoals.put(prmId, varGoal);
        return varGoal;
    }

    public List<clsSalesGoal> opGetAllSalesGoals() {
        try {
            List<clsSalesGoal> varList = attSalesGoalRepository.opGetAllSalesGoals();
            for (clsSalesGoal varGoal : varList) {
                if (varGoal.getAttEmployee() != null) {
                    try {
                        varGoal.setAttEmployee(attEmployeeService.opGetEmployeeById(varGoal.getAttEmployee().getAttEmployeeId()));
                    } catch (Exception ignored) {
                    }
                }
                opPopulateCompliance(varGoal);
            }
            return varList;
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener las metas de venta: ", e);
        }
    }

    public List<clsSalesGoal> opGetSalesGoalsByState(String prmState) {
        clsValidations.opValidateSalesGoalState(prmState);
        try {
            List<clsSalesGoal> varList = attSalesGoalRepository.opGetSalesGoalsByState(prmState);
            for (clsSalesGoal varGoal : varList) {
                if (varGoal.getAttEmployee() != null) {
                    try {
                        varGoal.setAttEmployee(attEmployeeService.opGetEmployeeById(varGoal.getAttEmployee().getAttEmployeeId()));
                    } catch (Exception ignored) {
                    }
                }
                opPopulateCompliance(varGoal);
            }
            return varList;
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener las metas de venta por estado: ", e);
        }
    }

    public List<clsSalesGoal> opGetSalesGoalsByEmployeeId(int prmEmpId) {
        clsValidations.opValidateId(prmEmpId);
        try {
            List<clsSalesGoal> varList = attSalesGoalRepository.opGetSalesGoalsByEmployeeId(prmEmpId);
            for (clsSalesGoal varGoal : varList) {
                if (varGoal.getAttEmployee() != null) {
                    try {
                        varGoal.setAttEmployee(attEmployeeService.opGetEmployeeById(varGoal.getAttEmployee().getAttEmployeeId()));
                    } catch (Exception ignored) {
                    }
                }
                opPopulateCompliance(varGoal);
            }
            return varList;
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener las metas del empleado: ", e);
        }
    }

    public clsSalesGoal opGetActiveGoalByEmployeeId(int prmEmpId) {
        clsValidations.opValidateId(prmEmpId);
        try {
            clsSalesGoal varGoal = attSalesGoalRepository.opGetActiveGoalByEmployeeId(prmEmpId);
            if (varGoal != null) {
                if (varGoal.getAttEmployee() != null) {
                    try {
                        varGoal.setAttEmployee(attEmployeeService.opGetEmployeeById(prmEmpId));
                    } catch (Exception ignored) {
                    }
                }
                opPopulateCompliance(varGoal);
            }
            return varGoal;
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener la meta activa del empleado: ", e);
        }
    }

    public double opGetEmployeeTotalSales(int prmEmpId) {
        clsValidations.opValidateId(prmEmpId);
        return attSalesGoalRepository.opGetAchievedSales(prmEmpId, null, null);
    }
}
