package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Error.excValidationException;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
import edu.unicauca.concesionaria.Repository.SalesGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesGoalServiceTest {

    @Mock
    private SalesGoalRepository salesGoalRepository;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private SalesGoalService salesGoalService;

    private clsEmployee mockEmployee;
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

        mockSalesGoal = new clsSalesGoal();
        mockSalesGoal.setAttSalesGoalId(10);
        mockSalesGoal.setAttEmployee(mockEmployee);
        mockSalesGoal.setAttGoalType("monthly");
        mockSalesGoal.setAttTargetValue(50000000.0);
        mockSalesGoal.setAttStartDate(Date.valueOf("2024-01-01"));
        mockSalesGoal.setAttEndDate(Date.valueOf("2024-01-31"));
        mockSalesGoal.setAttState("active");
    }

    // ==================== CÁLCULO DE FECHA FINAL ====================

    @Test
    @DisplayName("Debe calcular correctamente la fecha final según el tipo de meta")
    void testOpCalculateEndDate() {
        Date start = Date.valueOf("2024-01-01");

        // Monthly: +30 días
        Date monthlyEnd = SalesGoalService.opCalculateEndDate(start, "monthly");
        assertEquals(Date.valueOf("2024-01-31"), monthlyEnd);

        // Quarterly: +90 días
        Date quarterlyEnd = SalesGoalService.opCalculateEndDate(start, "quarterly");
        assertEquals(Date.valueOf("2024-03-31"), quarterlyEnd);

        // Yearly: +365 días
        Date yearlyEnd = SalesGoalService.opCalculateEndDate(start, "yearly");
        assertEquals(Date.valueOf("2024-12-31"), yearlyEnd);

        // Default / Otro: +30 días
        Date defaultEnd = SalesGoalService.opCalculateEndDate(start, "other");
        assertEquals(Date.valueOf("2024-01-31"), defaultEnd);

        // Nulos
        assertNull(SalesGoalService.opCalculateEndDate(null, "monthly"));
        assertNull(SalesGoalService.opCalculateEndDate(start, null));
    }

    // ==================== CÁLCULO DE CUMPLIMIENTO ====================

    @Test
    @DisplayName("Debe calcular el porcentaje de cumplimiento correctamente")
    void testOpPopulateCompliance_Success() {
        when(salesGoalRepository.opGetAchievedSales(eq(1000000001), any(), any()))
                .thenReturn(25000000.0);

        salesGoalService.opPopulateCompliance(mockSalesGoal);

        assertEquals(25000000.0, mockSalesGoal.getAttAchievedValue());
        assertEquals(50.0, mockSalesGoal.getAttCompliancePercentage());
    }

    @Test
    @DisplayName("Debe asignar 0.0 de cumplimiento cuando el valor objetivo es cero o negativo")
    void testOpPopulateCompliance_ZeroOrNegativeTarget() {
        mockSalesGoal.setAttTargetValue(0.0);
        when(salesGoalRepository.opGetAchievedSales(eq(1000000001), any(), any()))
                .thenReturn(10000.0);

        salesGoalService.opPopulateCompliance(mockSalesGoal);

        assertEquals(10000.0, mockSalesGoal.getAttAchievedValue());
        assertEquals(0.0, mockSalesGoal.getAttCompliancePercentage());
    }

    @Test
    @DisplayName("No debe fallar al calcular cumplimiento si la meta o el empleado son nulos")
    void testOpPopulateCompliance_NullGoalOrEmployee() {
        assertDoesNotThrow(() -> salesGoalService.opPopulateCompliance(null));

        clsSalesGoal goalWithoutEmployee = new clsSalesGoal();
        goalWithoutEmployee.setAttEmployee(null);
        assertDoesNotThrow(() -> salesGoalService.opPopulateCompliance(goalWithoutEmployee));
    }

    // ==================== REGISTRO DE METAS DE VENTA ====================

    @Test
    @DisplayName("Debe registrar exitosamente una meta de venta")
    void testOpRegisterSalesGoal_Success() {
        int goalId = 10;
        int empId = 1000000001;

        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(false);
        when(employeeService.opGetEmployeeById(empId)).thenReturn(mockEmployee);
        when(salesGoalRepository.opHasActiveGoal(empId)).thenReturn(false);
        when(salesGoalRepository.opGetSalesGoalById(goalId)).thenReturn(mockSalesGoal);

        assertDoesNotThrow(() -> salesGoalService.opRegisterSalesGoal(
                goalId, empId, "monthly", 50000000.0, Date.valueOf("2024-01-01"), "active"
        ));

        verify(salesGoalRepository, times(1)).opRegisterSalesGoal(any(clsSalesGoal.class));
    }

    @Test
    @DisplayName("Debe registrar una meta de venta trimestral exitosamente")
    void testOpRegisterSalesGoal_Quarterly() {
        int goalId = 20;
        int empId = 1000000001;
        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(false);
        when(employeeService.opGetEmployeeById(empId)).thenReturn(mockEmployee);
        when(salesGoalRepository.opHasActiveGoal(empId)).thenReturn(false);
        when(salesGoalRepository.opGetSalesGoalById(goalId)).thenReturn(mockSalesGoal);

        assertDoesNotThrow(() -> salesGoalService.opRegisterSalesGoal(
                goalId, empId, "quarterly", 80000000.0, Date.valueOf("2024-01-01"), "active"
        ));

        verify(salesGoalRepository, times(1)).opRegisterSalesGoal(argThat(g ->
                "quarterly".equals(g.getAttGoalType()) && g.getAttTargetValue() == 80000000.0
        ));
    }

    @Test
    @DisplayName("Debe lanzar excDuplicateDataException si la meta con ese ID ya existe")
    void testOpRegisterSalesGoal_DuplicateId() {
        int goalId = 10;
        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(true);

        assertThrows(excDuplicateDataException.class, () ->
                salesGoalService.opRegisterSalesGoal(
                        goalId, 1000000001, "monthly", 50000000.0, Date.valueOf("2024-01-01"), "active"
                )
        );

        verify(salesGoalRepository, never()).opRegisterSalesGoal(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si el empleado asignado está inactivo")
    void testOpRegisterSalesGoal_InactiveEmployee() {
        mockEmployee.setAttState("inactive");
        when(employeeService.opGetEmployeeById(1000000001)).thenReturn(mockEmployee);

        assertThrows(excValidationException.class, () ->
                salesGoalService.opRegisterSalesGoal(
                        null, 1000000001, "monthly", 50000000.0, Date.valueOf("2024-01-01"), "active"
                )
        );

        verify(salesGoalRepository, never()).opRegisterSalesGoal(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si el empleado ya tiene una meta activa")
    void testOpRegisterSalesGoal_AlreadyHasActiveGoal() {
        when(employeeService.opGetEmployeeById(1000000001)).thenReturn(mockEmployee);
        when(salesGoalRepository.opHasActiveGoal(1000000001)).thenReturn(true);

        assertThrows(excValidationException.class, () ->
                salesGoalService.opRegisterSalesGoal(
                        null, 1000000001, "monthly", 50000000.0, Date.valueOf("2024-01-01"), "active"
                )
        );

        verify(salesGoalRepository, never()).opRegisterSalesGoal(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException cuando la fecha de inicio es nula")
    void testOpRegisterSalesGoal_NullStartDate() {
        when(employeeService.opGetEmployeeById(1000000001)).thenReturn(mockEmployee);
        when(salesGoalRepository.opHasActiveGoal(1000000001)).thenReturn(false);

        assertThrows(excValidationException.class, () ->
                salesGoalService.opRegisterSalesGoal(
                        null, 1000000001, "monthly", 50000000.0, null, "active"
                )
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla el repositorio al registrar la meta")
    void testOpRegisterSalesGoal_DatabaseException() {
        when(employeeService.opGetEmployeeById(1000000001)).thenReturn(mockEmployee);
        when(salesGoalRepository.opHasActiveGoal(1000000001)).thenReturn(false);
        doThrow(new excDatabaseException("DB error")).when(salesGoalRepository).opRegisterSalesGoal(any());

        assertThrows(excDatabaseException.class, () ->
                salesGoalService.opRegisterSalesGoal(
                        null, 1000000001, "monthly", 50000000.0, Date.valueOf("2024-01-01"), "active"
                )
        );
    }

    // ==================== ACTUALIZACIÓN DE METAS DE VENTA ====================

    @Test
    @DisplayName("Debe actualizar una meta de venta exitosamente")
    void testOpUpdateSalesGoal_Success() {
        int goalId = 10;
        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(true);
        when(salesGoalRepository.opGetSalesGoalById(goalId)).thenReturn(mockSalesGoal);

        assertDoesNotThrow(() -> salesGoalService.opUpdateSalesGoal(
                goalId, 60000000.0, "quarterly", Date.valueOf("2024-02-01"), "active"
        ));

        verify(salesGoalRepository, times(1)).opUpdateSalesGoal(any(clsSalesGoal.class));
    }

    @Test
    @DisplayName("Debe lanzar excNotFoundException si la meta no existe")
    void testOpUpdateSalesGoal_NotFound() {
        int goalId = 999;
        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(false);

        assertThrows(excNotFoundException.class, () ->
                salesGoalService.opUpdateSalesGoal(
                        goalId, 60000000.0, "quarterly", Date.valueOf("2024-02-01"), "active"
                )
        );

        verify(salesGoalRepository, never()).opUpdateSalesGoal(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException al intentar modificar una meta ya completada")
    void testOpUpdateSalesGoal_AlreadyComplete() {
        int goalId = 10;
        mockSalesGoal.setAttState("complete");
        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(true);
        when(salesGoalRepository.opGetSalesGoalById(goalId)).thenReturn(mockSalesGoal);

        assertThrows(excValidationException.class, () ->
                salesGoalService.opUpdateSalesGoal(
                        goalId, 60000000.0, "quarterly", Date.valueOf("2024-02-01"), "active"
                )
        );

        verify(salesGoalRepository, never()).opUpdateSalesGoal(any());
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la actualización en BD")
    void testOpUpdateSalesGoal_DatabaseException() {
        int goalId = 10;
        when(salesGoalRepository.opSalesGoalExist(goalId)).thenReturn(true);
        when(salesGoalRepository.opGetSalesGoalById(goalId)).thenReturn(mockSalesGoal);
        doThrow(new excDatabaseException("DB error")).when(salesGoalRepository).opUpdateSalesGoal(any());

        assertThrows(excDatabaseException.class, () ->
                salesGoalService.opUpdateSalesGoal(
                        goalId, 60000000.0, "quarterly", Date.valueOf("2024-02-01"), "active"
                )
        );
    }

    // ==================== LISTAR METAS DE VENTA ====================

    @Test
    @DisplayName("Debe listar todas las metas de venta poblando datos de empleado y cumplimiento")
    void testOpGetAllSalesGoals_Success() {
        when(salesGoalRepository.opGetAllSalesGoals()).thenReturn(List.of(mockSalesGoal));
        when(employeeService.opGetEmployeeById(1000000001)).thenReturn(mockEmployee);
        when(salesGoalRepository.opGetAchievedSales(eq(1000000001), any(), any())).thenReturn(10000000.0);

        List<clsSalesGoal> list = salesGoalService.opGetAllSalesGoals();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).getAttSalesGoalId());
        assertEquals(10000000.0, list.get(0).getAttAchievedValue());
        verify(salesGoalRepository, times(1)).opGetAllSalesGoals();
    }

    @Test
    @DisplayName("Debe tolerar si la consulta del empleado asociado a la meta falla al listar")
    void testOpGetAllSalesGoals_EmployeeLookupFailsSilently() {
        when(salesGoalRepository.opGetAllSalesGoals()).thenReturn(List.of(mockSalesGoal));
        when(employeeService.opGetEmployeeById(1000000001)).thenThrow(new RuntimeException("Employee not found"));

        assertDoesNotThrow(() -> {
            List<clsSalesGoal> list = salesGoalService.opGetAllSalesGoals();
            assertEquals(1, list.size());
        });
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la BD al listar metas")
    void testOpGetAllSalesGoals_DatabaseException() {
        when(salesGoalRepository.opGetAllSalesGoals()).thenThrow(new excDatabaseException("DB error"));

        assertThrows(excDatabaseException.class, () -> salesGoalService.opGetAllSalesGoals());
    }

    // ==================== VENTAS TOTALES DEL EMPLEADO ====================

    @Test
    @DisplayName("Debe obtener las ventas totales acumuladas de un empleado")
    void testOpGetEmployeeTotalSales() {
        int empId = 1000000001;
        when(salesGoalRepository.opGetAchievedSales(empId, null, null)).thenReturn(45000000.0);

        double total = salesGoalService.opGetEmployeeTotalSales(empId);

        assertEquals(45000000.0, total);
        verify(salesGoalRepository, times(1)).opGetAchievedSales(empId, null, null);

        assertThrows(excValidationException.class, () -> salesGoalService.opGetEmployeeTotalSales(0));
    }
}
