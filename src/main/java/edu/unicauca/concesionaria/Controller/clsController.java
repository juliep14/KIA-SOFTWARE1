package edu.unicauca.concesionaria.Controller;

import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
import edu.unicauca.concesionaria.Service.CustomerService;
import edu.unicauca.concesionaria.Service.EmployeeService;
import edu.unicauca.concesionaria.Service.SalesGoalService;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;

@Component
public class clsController {

    private final CustomerService attCustomerService;
    private final EmployeeService attEmployeeService;
    private final SalesGoalService attSalesGoalService;

    public clsController(CustomerService prmCustomerService,
                         EmployeeService prmEmployeeService,
                         SalesGoalService prmSalesGoalService) {
        this.attCustomerService = prmCustomerService;
        this.attEmployeeService = prmEmployeeService;
        this.attSalesGoalService = prmSalesGoalService;
    }

    // region EMPLOYEE

    public void opRegisterEmployee(int prmIdEmployee, String prmName,
            String prmPhone, double prmSalary, Date prmHireDate, String prmRole, String prmState) {
        attEmployeeService.opRegisterEmployee(
                prmIdEmployee, prmName, prmPhone, prmSalary, prmHireDate, prmRole, prmState);
    }

    public void opUpdateEmployee(int prmId, String prmName, String prmPhone, Double prmSalary, String prmState) {
        attEmployeeService.opUpdateEmployee(prmId, prmName, prmPhone, prmSalary, prmState);
    }

    public clsEmployee opGetEmployeeById(int prmId) {
        return attEmployeeService.opGetEmployeeById(prmId);
    }

    public List<clsEmployee> opGetAllEmployees() {
        return attEmployeeService.opGetAllEmployees();
    }

    // endregion

    // region CUSTOMER

    public void opRegisterCustomer(Integer prmId, String prmName, String prmPhone,
            String prmEmail, String prmState) {
        attCustomerService.opRegisterCustomer(prmId, prmName, prmEmail, prmPhone, prmState);
    }

    public void opUpdateCustomer(int prmCurrentId, int prmNewId, String prmName, String prmPhone, String prmEmail) {
        attCustomerService.opUpdateCustomer(prmCurrentId, prmNewId, prmName, prmPhone, prmEmail);
    }

    public void opUpdateCustomer(int prmId, String prmName, String prmPhone, String prmEmail) {
        attCustomerService.opUpdateCustomer(prmId, prmId, prmName, prmPhone, prmEmail);
    }

    public clsCustomer opGetCustomerForEdit(int prmId) {
        return attCustomerService.opGetCustomerForEdit(prmId);
    }

    public void opDisableCustomer(int prmId) {
        attCustomerService.opDisableCustomer(prmId);
    }

    public clsCustomer opGetCustomerById(int prmId) {
        return attCustomerService.opGetCustomerById(prmId);
    }

    public List<clsCustomer> opGetAllCustomers() {
        return attCustomerService.opGetAllCustomers();
    }

    // endregion

    // region SALESGOAL

    public int opRegisterSalesGoal(Integer prmSalesGoalId, int prmIdEmployee,
                                   String prmGoalType, double prmTargetValue, Date prmStartDate, String prmGoalState) {
        return attSalesGoalService.opRegisterSalesGoal(
                prmSalesGoalId, prmIdEmployee, prmGoalType, prmTargetValue, prmStartDate, prmGoalState);
    }

    public void opUpdateSalesGoal(int prmId, Double prmTargetValue, String prmGoalType) {
        attSalesGoalService.opUpdateSalesGoal(prmId, prmTargetValue, prmGoalType);
    }

    public void opDisableSalesGoal(int prmId) {
        attSalesGoalService.opDisableSalesGoal(prmId);
    }

    public void opCompleteSalesGoal(int prmId) {
        attSalesGoalService.opCompleteSalesGoal(prmId);
    }

    public clsSalesGoal opGetSalesGoalById(int prmId) {
        return attSalesGoalService.opGetSalesGoalById(prmId);
    }

    public List<clsSalesGoal> opGetAllSalesGoals() {
        return attSalesGoalService.opGetAllSalesGoals();
    }

    public List<clsSalesGoal> opGetSalesGoalsByState(String prmState) {
        return attSalesGoalService.opGetSalesGoalsByState(prmState);
    }

    public List<clsSalesGoal> opGetSalesGoalsByEmployeeId(int prmEmployeeId) {
        return attSalesGoalService.opGetSalesGoalsByEmployeeId(prmEmployeeId);
    }

    public clsSalesGoal opGetActiveGoalByEmployeeId(int prmEmployeeId) {
        return attSalesGoalService.opGetActiveGoalByEmployeeId(prmEmployeeId);
    }

    public double opGetEmployeeTotalSales(int prmEmployeeId) {
        return attSalesGoalService.opGetEmployeeTotalSales(prmEmployeeId);
    }

    // endregion
}
