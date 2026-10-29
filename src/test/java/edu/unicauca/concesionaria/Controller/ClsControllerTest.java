package edu.unicauca.concesionaria.Controller;

import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
import edu.unicauca.concesionaria.Service.CustomerService;
import edu.unicauca.concesionaria.Service.EmployeeService;
import edu.unicauca.concesionaria.Service.SalesGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClsControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private SalesGoalService salesGoalService;

    @InjectMocks
    private clsController controller;

    private clsEmployee mockEmployee;
    private clsCustomer mockCustomer;
    private clsSalesGoal mockSalesGoal;

    @BeforeEach
    void setUp() {
        mockEmployee = new clsEmployee();
        mockEmployee.setAttEmployeeId(1000000001);
        mockEmployee.setAttName("Carlos Ruiz");
        mockEmployee.setAttPhone("3001234567");
        mockEmployee.setAttSalary(2500000.0);
        mockEmployee.setAttHireDate(Date.valueOf("2023-05-15"));
        mockEmployee.setAttRole("seller");
        mockEmployee.setAttState("active");

        mockCustomer = new clsCustomer();
        mockCustomer.setAttCustomerId(1);
        mockCustomer.setAttName("Ana Gomez");
        mockCustomer.setAttPhone("3109876543");
        mockCustomer.setAttEmail("ana@gmail.com");
        mockCustomer.setAttState("active");

        mockSalesGoal = new clsSalesGoal();
        mockSalesGoal.setAttSalesGoalId(10);
        mockSalesGoal.setAttEmployee(mockEmployee);
        mockSalesGoal.setAttGoalType("monthly");
        mockSalesGoal.setAttTargetValue(50000000.0);
        mockSalesGoal.setAttStartDate(Date.valueOf("2024-01-01"));
        mockSalesGoal.setAttEndDate(Date.valueOf("2024-01-31"));
        mockSalesGoal.setAttState("active");
    }

    // ==================== SECCIÓN: EMPLOYEE ====================

    @Test
    @DisplayName("Debe delegar el registro de empleado al EmployeeService")
    void testOpRegisterEmployee_DelegatesToService() {
        int id = 1000000001;
        String name = "Carlos Ruiz";
        String phone = "3001234567";
        double salary = 2500000.0;
        Date hireDate = Date.valueOf("2023-05-15");
        String role = "seller";
        String state = "active";

        controller.opRegisterEmployee(id, name, phone, salary, hireDate, role, state);

        verify(employeeService, times(1)).opRegisterEmployee(id, name, phone, salary, hireDate, role, state);
    }

    @Test
    @DisplayName("Debe delegar la actualización de empleado al EmployeeService")
    void testOpUpdateEmployee_DelegatesToService() {
        int id = 1000000001;
        String name = "Carlos Ruiz Editado";
        String phone = "3111234567";
        Double salary = 2800000.0;
        String state = "inactive";

        controller.opUpdateEmployee(id, name, phone, salary, state);

        verify(employeeService, times(1)).opUpdateEmployee(id, name, phone, salary, state);
    }

    @Test
    @DisplayName("Debe delegar la consulta de empleado por ID al EmployeeService y retornar resultado")
    void testOpGetEmployeeById_ReturnsCorrectEmployee() {
        int id = 1000000001;
        when(employeeService.opGetEmployeeById(id)).thenReturn(mockEmployee);

        clsEmployee result = controller.opGetEmployeeById(id);

        assertNotNull(result);
        assertEquals(1000000001, result.getAttEmployeeId());
        assertEquals("Carlos Ruiz", result.getAttName());
        verify(employeeService, times(1)).opGetEmployeeById(id);
    }

    @Test
    @DisplayName("Debe delegar la obtención de todos los empleados al EmployeeService")
    void testOpGetAllEmployees_ReturnsList() {
        when(employeeService.opGetAllEmployees()).thenReturn(List.of(mockEmployee));

        List<clsEmployee> result = controller.opGetAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Carlos Ruiz", result.get(0).getAttName());
        verify(employeeService, times(1)).opGetAllEmployees();
    }

    // ==================== SECCIÓN: CUSTOMER ====================

    @Test
    @DisplayName("Debe delegar el registro de cliente al CustomerService")
    void testOpRegisterCustomer_DelegatesToService() {
        int id = 1;
        String name = "Ana Gomez";
        String phone = "3109876543";
        String email = "ana@gmail.com";
        String state = "active";

        controller.opRegisterCustomer(id, name, phone, email, state);

        // Nota: controller envía (id, name, email, phone, state) a customerService.opRegisterCustomer
        verify(customerService, times(1)).opRegisterCustomer(id, name, email, phone, state);
    }

    @Test
    @DisplayName("Debe delegar la actualización de cliente al CustomerService")
    void testOpUpdateCustomer_DelegatesToService() {
        int id = 1;
        String name = "Ana Gomez Editada";
        String phone = "3109876543";
        String email = "nuevo@gmail.com";
        String state = "inactive";

        controller.opUpdateCustomer(id, name, phone, email, state);

        verify(customerService, times(1)).opUpdateCustomer(id, name, phone, email, state);
    }

    @Test
    @DisplayName("Debe delegar la obtención de todos los clientes al CustomerService")
    void testOpGetAllCustomers_ReturnsList() {
        when(customerService.opGetAllCustomers()).thenReturn(List.of(mockCustomer));

        List<clsCustomer> result = controller.opGetAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ana Gomez", result.get(0).getAttName());
        verify(customerService, times(1)).opGetAllCustomers();
    }

    // ==================== SECCIÓN: SALESGOAL ====================

    @Test
    @DisplayName("Debe delegar el registro de meta de venta al SalesGoalService")
    void testOpRegisterSalesGoal_DelegatesToService() {
        int goalId = 10;
        int empId = 1000000001;
        String type = "monthly";
        double target = 50000000.0;
        Date startDate = Date.valueOf("2024-01-01");
        String state = "active";

        controller.opRegisterSalesGoal(goalId, empId, type, target, startDate, state);

        verify(salesGoalService, times(1)).opRegisterSalesGoal(goalId, empId, type, target, startDate, state);
    }

    @Test
    @DisplayName("Debe delegar la actualización de meta de venta al SalesGoalService")
    void testOpUpdateSalesGoal_DelegatesToService() {
        int goalId = 10;
        Double target = 60000000.0;
        String type = "quarterly";
        Date startDate = Date.valueOf("2024-02-01");
        String state = "active";

        controller.opUpdateSalesGoal(goalId, target, type, startDate, state);

        verify(salesGoalService, times(1)).opUpdateSalesGoal(goalId, target, type, startDate, state);
    }

    @Test
    @DisplayName("Debe delegar la obtención de todas las metas de venta al SalesGoalService")
    void testOpGetAllSalesGoals_ReturnsList() {
        when(salesGoalService.opGetAllSalesGoals()).thenReturn(List.of(mockSalesGoal));

        List<clsSalesGoal> result = controller.opGetAllSalesGoals();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getAttSalesGoalId());
        verify(salesGoalService, times(1)).opGetAllSalesGoals();
    }

    @Test
    @DisplayName("Debe delegar el cálculo de ventas totales del empleado al SalesGoalService")
    void testOpGetEmployeeTotalSales_ReturnsCalculatedValue() {
        int employeeId = 1000000001;
        double expectedSales = 15000000.0;
        when(salesGoalService.opGetEmployeeTotalSales(employeeId)).thenReturn(expectedSales);

        double actualSales = controller.opGetEmployeeTotalSales(employeeId);

        assertEquals(expectedSales, actualSales);
        verify(salesGoalService, times(1)).opGetEmployeeTotalSales(employeeId);
    }
}
