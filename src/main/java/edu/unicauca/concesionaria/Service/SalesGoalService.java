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
            case "monthly" ->
                varStart.plusDays(30);
            case "quarterly" ->
                varStart.plusDays(90);
            case "yearly" ->
                varStart.plusDays(365);
            default ->
                varStart.plusDays(30);
        };
        return Date.valueOf(varEnd);
    }

    public void opRegisterSalesGoal(Integer prmSalesGoalId, int prmEmployeeId,
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

        try {
            attSalesGoalRepository.opRegisterSalesGoal(varTemp);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al registrar la meta de venta: ", e);
        }

        opRefreshCache(prmSalesGoalId);
    }

    public void opUpdateSalesGoal(int prmId, Double prmTargetValue, String prmGoalType, Date prmStartDate, String prmGoalState) {
        clsValidations.opValidateId(prmId);

        if (!attSalesGoalRepository.opSalesGoalExist(prmId)) {
            throw new excNotFoundException("Meta de venta no encontrada");
        }

        clsSalesGoal varGoal = opFetchFromDB(prmId);

        if ("complete".equalsIgnoreCase(varGoal.getAttState())) {
            throw new excValidationException("No se puede modificar una meta ya completada");
        }        

        clsValidations.opValidateGoalType(prmGoalType);
        clsValidations.opValidateTargetValue(prmTargetValue);
        clsValidations.opValidateSalesGoalState(prmGoalState);
        
        if (prmTargetValue != null) {
            varGoal.setAttTargetValue(prmTargetValue);
        }       
        if (prmStartDate != null) {
            clsValidations.opValidateDate(prmStartDate);
            varGoal.setAttStartDate(prmStartDate);
        }
        if (prmGoalType != null && !prmGoalType.isBlank()) {
            varGoal.setAttGoalType(prmGoalType.toLowerCase().trim());
        }
        if (varGoal.getAttStartDate() != null && varGoal.getAttGoalType() != null) {
            Date varStartDate = varGoal.getAttStartDate() instanceof Date d
                    ? d
                    : new Date(varGoal.getAttStartDate().getTime());

            varGoal.setAttEndDate(opCalculateEndDate(varStartDate, varGoal.getAttGoalType()));
        }
        
        varGoal.setAttState(prmGoalState);       
        try {
            attSalesGoalRepository.opUpdateSalesGoal(varGoal);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al actualizar la meta de venta: ", e);
        }

        opRefreshCache(prmId);
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

    public double opGetEmployeeTotalSales(int prmEmpId) {
        clsValidations.opValidateId(prmEmpId);
        return attSalesGoalRepository.opGetAchievedSales(prmEmpId, null, null);
    }
}
