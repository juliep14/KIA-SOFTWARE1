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

    public void opRegisterCustomer(int prmId, String prmName, String prmPhone,
            String prmEmail, String prmState) {
        attCustomerService.opRegisterCustomer(prmId, prmName, prmEmail, prmPhone, prmState);
    }

    public void opUpdateCustomer(int prmId, String prmName, String prmPhone, String prmEmail, String prmState) {
        attCustomerService.opUpdateCustomer(prmId, prmName, prmPhone, prmEmail, prmState);
    }

    public List<clsCustomer> opGetAllCustomers() {
        return attCustomerService.opGetAllCustomers();
    }

    // endregion

    // region SALESGOAL

    public void opRegisterSalesGoal(int prmSalesGoalId, int prmIdEmployee,
                                   String prmGoalType, double prmTargetValue, Date prmStartDate, String prmGoalState) {
        attSalesGoalService.opRegisterSalesGoal(
                prmSalesGoalId, prmIdEmployee, prmGoalType, prmTargetValue, prmStartDate, prmGoalState);
    }

    public void opUpdateSalesGoal(int prmId, Double prmTargetValue, String prmGoalType,  Date prmStartDate, String prmGoalState) {
        attSalesGoalService.opUpdateSalesGoal(prmId, prmTargetValue, prmGoalType, prmStartDate, prmGoalState);
    }

    public List<clsSalesGoal> opGetAllSalesGoals() {
        return attSalesGoalService.opGetAllSalesGoals();
    }

    public double opGetEmployeeTotalSales(int prmEmployeeId) {
        return attSalesGoalService.opGetEmployeeTotalSales(prmEmployeeId);
    }

    // endregion
}
