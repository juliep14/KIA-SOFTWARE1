package edu.unicauca.concesionaria.Controller;

import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Service.CustomerService;
import edu.unicauca.concesionaria.Service.EmployeeService;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;

@Component
public class clsController {

    private final CustomerService attCustomerService;
    private final EmployeeService attEmployeeService;

    public clsController(CustomerService prmCustomerService, EmployeeService prmEmployeeService) {
        this.attCustomerService = prmCustomerService;
        this.attEmployeeService = prmEmployeeService;
    }

    public void opRegisterEmployee(int prmIdEmployee, String prmName,
            String prmPhone, double prmSalary, Date prmHireDate, String prmRole, String prmState) {
        attEmployeeService.opRegisterEmployee(
                prmIdEmployee, prmName, prmPhone, prmSalary, prmHireDate, prmRole, prmState);
    }

    public void opUpdateEmployee(int prmId, String prmName, String prmPhone, Double prmSalary) {
        attEmployeeService.opUpdateEmployee(prmId, prmName, prmPhone, prmSalary);
    }

    public void opDisableEmployee(int prmId) {
        attEmployeeService.opDisableEmployee(prmId);
    }

    public clsEmployee opGetEmployeeById(int prmId) {
        return attEmployeeService.opGetEmployeeById(prmId);
    }

    public List<clsEmployee> opGetAllEmployees() {
        return attEmployeeService.opGetAllEmployees();
    }

    public void opRegisterCustomer(int prmId, String prmName, String prmPhone,
            String prmEmail, String prmState) {
        attCustomerService.opRegisterCustomer(prmId, prmName, prmEmail, prmPhone, prmState);
    }

    public void opUpdateCustomer(int prmId, String prmName, String prmPhone, String prmEmail) {
        attCustomerService.opUpdateCustomer(prmId, prmName, prmPhone, prmEmail);
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
}
