package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Error.excValidationException;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Repository.EmployeeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private clsEmployee mockEmployee;

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
    }

    // ==================== REGISTRO DE EMPLEADOS ====================

    @Test
    @DisplayName("Debe registrar exitosamente un empleado vendedor")
    void testOpRegisterEmployee_SuccessfulRegistration() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(false);
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(mockEmployee);

        assertDoesNotThrow(() -> employeeService.opRegisterEmployee(
                id, "Carlos Ruiz", "3001234567", 2500000.0,
                Date.valueOf("2023-05-15"), "seller", "active"
        ));

        verify(employeeRepository, times(1)).opEmployeeExist(id);
        verify(employeeRepository, times(1)).opRegisterEmployee(any(clsEmployee.class));
        verify(employeeRepository, times(1)).opGetEmployeeById(id);
    }

    @Test
    @DisplayName("Debe registrar exitosamente un empleado manager si no existe otro")
    void testOpRegisterEmployee_SuccessfulManagerRegistration() {
        int id = 1000000002;
        clsEmployee mockManager = new clsEmployee();
        mockManager.setAttEmployeeId(id);
        mockManager.setAttName("Ana Gómez");
        mockManager.setAttPhone("3109876543");
        mockManager.setAttSalary(6000000.0);
        mockManager.setAttHireDate(Date.valueOf("2023-01-10"));
        mockManager.setAttRole("manager");
        mockManager.setAttState("active");

        when(employeeRepository.opEmployeeExist(id)).thenReturn(false);
        when(employeeRepository.opManagerExists()).thenReturn(false);
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(mockManager);

        assertDoesNotThrow(() -> employeeService.opRegisterEmployee(
                id, "Ana Gómez", "3109876543", 6000000.0,
                Date.valueOf("2023-01-10"), "manager", "active"
        ));

        verify(employeeRepository, times(1)).opManagerExists();
        verify(employeeRepository, times(1)).opRegisterEmployee(any(clsEmployee.class));
    }

    @Test
    @DisplayName("Debe lanzar excDuplicateDataException cuando el ID ya existe")
    void testOpRegisterEmployee_ThrowsDuplicateDataException() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(true);

        assertThrows(excDuplicateDataException.class, () ->
                employeeService.opRegisterEmployee(
                        id, "Carlos Ruiz", "3001234567", 2500000.0,
                        Date.valueOf("2023-05-15"), "seller", "active"
                )
        );

        verify(employeeRepository, never()).opRegisterEmployee(any(clsEmployee.class));
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si ya existe un gerente general")
    void testOpRegisterEmployee_ThrowsValidationExceptionForMultipleManagers() {
        int id = 1000000002;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(false);
        when(employeeRepository.opManagerExists()).thenReturn(true);

        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(
                        id, "Ana Gomez", "3109876543", 5000000.0,
                        Date.valueOf("2022-01-10"), "manager", "active"
                )
        );

        verify(employeeRepository, never()).opRegisterEmployee(any(clsEmployee.class));
    }

    @Test
    @DisplayName("Debe lanzar excValidationException cuando el ID es inválido")
    void testOpRegisterEmployee_InvalidId() {
        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(
                        0, "Carlos Ruiz", "3001234567", 2500000.0,
                        Date.valueOf("2023-05-15"), "seller", "active"
                )
        );
        verify(employeeRepository, never()).opEmployeeExist(anyInt());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException cuando los campos no cumplen validación")
    void testOpRegisterEmployee_InvalidFields() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(false);

        // Nombre inválido
        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(id, "Ca", "3001234567", 2500000.0,
                        Date.valueOf("2023-05-15"), "seller", "active"));

        // Teléfono inválido
        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(id, "Carlos Ruiz", "123", 2500000.0,
                        Date.valueOf("2023-05-15"), "seller", "active"));

        // Salario negativo
        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(id, "Carlos Ruiz", "3001234567", -100.0,
                        Date.valueOf("2023-05-15"), "seller", "active"));

        // Rol inválido
        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(id, "Carlos Ruiz", "3001234567", 2500000.0,
                        Date.valueOf("2023-05-15"), "supervisor", "active"));

        // Estado inválido
        assertThrows(excValidationException.class, () ->
                employeeService.opRegisterEmployee(id, "Carlos Ruiz", "3001234567", 2500000.0,
                        Date.valueOf("2023-05-15"), "seller", "activo"));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si el repositorio falla al registrar")
    void testOpRegisterEmployee_DatabaseException() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(false);
        doThrow(new excDatabaseException("DB error")).when(employeeRepository).opRegisterEmployee(any());

        assertThrows(excDatabaseException.class, () ->
                employeeService.opRegisterEmployee(
                        id, "Carlos Ruiz", "3001234567", 2500000.0,
                        Date.valueOf("2023-05-15"), "seller", "active"
                )
        );
    }

    // ==================== ACTUALIZACIÓN DE EMPLEADOS ====================

    @Test
    @DisplayName("Debe actualizar exitosamente un empleado existente")
    void testOpUpdateEmployee_Success() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(true);
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(mockEmployee);

        assertDoesNotThrow(() -> employeeService.opUpdateEmployee(
                id, "Carlos Ruiz Editado", "3119876543", 3000000.0, "inactive"
        ));

        verify(employeeRepository, times(1)).opUpdateEmployee(any(clsEmployee.class));
    }

    @Test
    @DisplayName("Debe actualizar un empleado permitiendo campos opcionales nulos")
    void testOpUpdateEmployee_PartialNullFields() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(true);
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(mockEmployee);

        assertDoesNotThrow(() -> employeeService.opUpdateEmployee(id, "Carlos Ruiz", null, 2500000.0, null));
        verify(employeeRepository, times(1)).opUpdateEmployee(any(clsEmployee.class));
    }

    @Test
    @DisplayName("Debe lanzar excNotFoundException al actualizar un empleado que no existe")
    void testOpUpdateEmployee_NotFound() {
        int id = 999;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(false);

        assertThrows(excNotFoundException.class, () ->
                employeeService.opUpdateEmployee(id, "Carlos Ruiz", "3001234567", 2500000.0, "active")
        );
        verify(employeeRepository, never()).opUpdateEmployee(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException al actualizar con ID inválido")
    void testOpUpdateEmployee_InvalidId() {
        assertThrows(excValidationException.class, () ->
                employeeService.opUpdateEmployee(-1, "Carlos Ruiz", "3001234567", 2500000.0, "active")
        );
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si algún campo a actualizar no cumple validación")
    void testOpUpdateEmployee_InvalidFieldValues() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(true);

        assertThrows(excValidationException.class, () ->
                employeeService.opUpdateEmployee(id, "C", "3001234567", 2500000.0, "active"));
        assertThrows(excValidationException.class, () ->
                employeeService.opUpdateEmployee(id, "Carlos Ruiz", "12", 2500000.0, "active"));
        assertThrows(excValidationException.class, () ->
                employeeService.opUpdateEmployee(id, "Carlos Ruiz", "3001234567", -1.0, "active"));
        assertThrows(excValidationException.class, () ->
                employeeService.opUpdateEmployee(id, "Carlos Ruiz", "3001234567", 2500000.0, "pendiente"));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si el repositorio falla al actualizar")
    void testOpUpdateEmployee_DatabaseException() {
        int id = 1000000001;
        when(employeeRepository.opEmployeeExist(id)).thenReturn(true);
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(mockEmployee);
        doThrow(new excDatabaseException("DB error")).when(employeeRepository).opUpdateEmployee(any());

        assertThrows(excDatabaseException.class, () ->
                employeeService.opUpdateEmployee(id, "Carlos Ruiz", "3001234567", 2500000.0, "active")
        );
    }

    // ==================== CONSULTA DE EMPLEADOS Y CACHÉ ====================

    @Test
    @DisplayName("Debe consultar empleado por ID y almacenarlo en caché para llamadas posteriores")
    void testOpGetEmployeeById_CacheHit() {
        int id = 1000000001;
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(mockEmployee);

        // Primera llamada: busca en BD
        clsEmployee firstCall = employeeService.opGetEmployeeById(id);
        assertNotNull(firstCall);
        assertEquals(id, firstCall.getAttEmployeeId());

        // Segunda llamada: recupera de caché, sin invocar repositorio nuevamente
        clsEmployee secondCall = employeeService.opGetEmployeeById(id);
        assertSame(firstCall, secondCall);

        verify(employeeRepository, times(1)).opGetEmployeeById(id);
    }

    @Test
    @DisplayName("Debe lanzar excNotFoundException cuando el empleado no existe en BD")
    void testOpGetEmployeeById_NotFound() {
        int id = 999;
        when(employeeRepository.opGetEmployeeById(id)).thenReturn(null);

        assertThrows(excNotFoundException.class, () -> employeeService.opGetEmployeeById(id));
    }

    @Test
    @DisplayName("Debe lanzar excValidationException cuando el ID a consultar es inválido")
    void testOpGetEmployeeById_InvalidId() {
        assertThrows(excValidationException.class, () -> employeeService.opGetEmployeeById(0));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la BD al consultar por ID")
    void testOpGetEmployeeById_DatabaseException() {
        int id = 1000000001;
        when(employeeRepository.opGetEmployeeById(id)).thenThrow(new excDatabaseException("DB error"));

        assertThrows(excDatabaseException.class, () -> employeeService.opGetEmployeeById(id));
    }

    // ==================== LISTAR TODOS LOS EMPLEADOS ====================

    @Test
    @DisplayName("Debe listar todos los empleados")
    void testOpGetAllEmployees_Success() {
        when(employeeRepository.opGetAllEmployees()).thenReturn(List.of(mockEmployee));

        List<clsEmployee> list = employeeService.opGetAllEmployees();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(1000000001, list.get(0).getAttEmployeeId());

        verify(employeeRepository, times(1)).opGetAllEmployees();
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta de todos los empleados")
    void testOpGetAllEmployees_DatabaseException() {
        when(employeeRepository.opGetAllEmployees()).thenThrow(new excDatabaseException("DB error"));

        assertThrows(excDatabaseException.class, () -> employeeService.opGetAllEmployees());
    }

    // ==================== VERIFICACIÓN DE EXISTENCIA ====================

    @Test
    @DisplayName("Debe verificar correctamente la existencia de un empleado")
    void testOpEmployeeExist() {
        when(employeeRepository.opEmployeeExist(1000000001)).thenReturn(true);
        when(employeeRepository.opEmployeeExist(999)).thenReturn(false);

        assertTrue(employeeService.opEmployeeExist(1000000001));
        assertFalse(employeeService.opEmployeeExist(999));

        assertThrows(excValidationException.class, () -> employeeService.opEmployeeExist(0));
    }
}
