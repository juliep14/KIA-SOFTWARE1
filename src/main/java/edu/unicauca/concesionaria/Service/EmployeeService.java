package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Error.excValidationException;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository attEmployeeRepository;
    private HashMap<Integer, clsEmployee> attEmployees = new HashMap<>();

    public EmployeeService(EmployeeRepository prmEmployeeRepository) {
        this.attEmployeeRepository = prmEmployeeRepository;
    }

    private clsEmployee opFetchFromDB(int prmId) {
        clsEmployee varEmployee;
        try {
            varEmployee = attEmployeeRepository.opGetEmployeeById(prmId);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener el empleado desde BD: ", e);
        }
        if (varEmployee == null) {
            throw new excNotFoundException("Empleado no encontrado");
        }
        return varEmployee;
    }

    private void opRefreshCache(int prmId) {
        attEmployees.remove(prmId);
        clsEmployee varFresh = opFetchFromDB(prmId);
        attEmployees.put(prmId, varFresh);
    }

    public void opRegisterEmployee(int prmId, String prmName, String prmPhone,
            double prmSalary, Date prmHireDate, String prmRole, String prmState) {

        clsValidations.opValidateId(prmId);

        if (attEmployeeRepository.opEmployeeExist(prmId)) {
            throw new excDuplicateDataException("Empleado ya registrado");
        }

        clsValidations.opValidateName(prmName, 3, 50);
        clsValidations.opValidatePhone(prmPhone);
        clsValidations.opValidateSalary(prmSalary);
        clsValidations.opValidateDate(prmHireDate);
        clsValidations.opValidateRole(prmRole);
        clsValidations.opValidateState(prmState);

        String varRole = prmRole.toLowerCase().trim();
        if (varRole.equals("manager") && attEmployeeRepository.opManagerExists()) {
            throw new excValidationException("Error: Solo se permite un gerente general");
        }

        clsEmployee varTemp = new clsEmployee();
        varTemp.setAttEmployeeId(prmId);
        varTemp.setAttName(prmName.trim());
        varTemp.setAttPhone(prmPhone);
        varTemp.setAttSalary(prmSalary);
        varTemp.setAttHireDate(prmHireDate);
        varTemp.setAttRole(varRole);
        varTemp.setAttState(prmState.toLowerCase().trim());

        try {
            attEmployeeRepository.opRegisterEmployee(varTemp);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al registrar empleado: ", e);
        }
        opRefreshCache(prmId);
    }

    public void opUpdateEmployee(int prmId, String prmName, String prmPhone, Double prmSalary, String prmState) {
        clsValidations.opValidateId(prmId);

        if (!attEmployeeRepository.opEmployeeExist(prmId)) {
            throw new excNotFoundException("Empleado no encontrado");
        }

        if (prmName != null) 
            clsValidations.opValidateName(prmName, 3, 50);      
        if (prmPhone != null) 
            clsValidations.opValidatePhone(prmPhone);       
        if (prmSalary != null) 
            clsValidations.opValidateSalary(prmSalary);        
        if (prmState != null) 
            clsValidations.opValidateState(prmState);       

        clsEmployee varEmployee = opFetchFromDB(prmId);
        varEmployee.setAttName(prmName.trim());
        varEmployee.setAttPhone(prmPhone);
        varEmployee.setAttSalary(prmSalary);
        varEmployee.setAttState(prmState);

        try {
            attEmployeeRepository.opUpdateEmployee(varEmployee);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al actualizar empleado: ", e);
        }

        opRefreshCache(prmId);
    }

    public List<clsEmployee> opGetAllEmployees() {
        try {
            return attEmployeeRepository.opGetAllEmployees();
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener empleados: ", e);
        }
    }

    public clsEmployee opGetEmployeeById(int prmId) {
        clsValidations.opValidateId(prmId);

        clsEmployee varEmployee = attEmployees.get(prmId);
        if (varEmployee != null) {
            return varEmployee;
        }

        varEmployee = opFetchFromDB(prmId);
        attEmployees.put(prmId, varEmployee);
        return varEmployee;
    }

    public boolean opEmployeeExist(int prmId) {
        clsValidations.opValidateId(prmId);
        return attEmployeeRepository.opEmployeeExist(prmId);
    }
}
