package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Model.clsEmployee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private EmployeeRepository employeeRepository;

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

    @Test
    @DisplayName("Debe registrar un empleado correctamente en base de datos")
    void testOpRegisterEmployee_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> employeeRepository.opRegisterEmployee(mockEmployee));

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq(1000000001),
                eq("Carlos Ruiz"),
                eq("3001234567"),
                eq(2500000.0),
                eq(Date.valueOf("2023-05-15")),
                eq("seller"),
                eq("active")
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla el registro en BD")
    void testOpRegisterEmployee_ThrowsException() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DataAccessException("ORA-00001: unique constraint violated") {});

        assertThrows(excDatabaseException.class, () -> employeeRepository.opRegisterEmployee(mockEmployee));
    }

    @Test
    @DisplayName("Debe actualizar un empleado correctamente en base de datos")
    void testOpUpdateEmployee_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> employeeRepository.opUpdateEmployee(mockEmployee));

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq("Carlos Ruiz"),
                eq("3001234567"),
                eq(2500000.0),
                eq("active"),
                eq(1000000001)
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la actualización en BD")
    void testOpUpdateEmployee_ThrowsException() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> employeeRepository.opUpdateEmployee(mockEmployee));
    }

    @Test
    @DisplayName("Debe retornar true si el empleado existe")
    void testOpEmployeeExist_ReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1000000001)))
                .thenReturn(1);

        boolean exists = employeeRepository.opEmployeeExist(1000000001);

        assertTrue(exists);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class), eq(1000000001));
    }

    @Test
    @DisplayName("Debe retornar false si el empleado no existe o count es null")
    void testOpEmployeeExist_ReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(999)))
                .thenReturn(0);

        boolean exists = employeeRepository.opEmployeeExist(999);
        assertFalse(exists);

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(888)))
                .thenReturn(null);
        assertFalse(employeeRepository.opEmployeeExist(888));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la verificación de existencia")
    void testOpEmployeeExist_ThrowsException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenThrow(new DataAccessException("DB timeout") {});

        assertThrows(excDatabaseException.class, () -> employeeRepository.opEmployeeExist(1000000001));
    }

    @Test
    @DisplayName("Debe retornar true si ya existe un gerente general")
    void testOpManagerExists_ReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);

        boolean exists = employeeRepository.opManagerExists();

        assertTrue(exists);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
    }

    @Test
    @DisplayName("Debe retornar false si no existe un gerente general")
    void testOpManagerExists_ReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(0);

        assertFalse(employeeRepository.opManagerExists());
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta de gerente")
    void testOpManagerExists_ThrowsException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> employeeRepository.opManagerExists());
    }

    @Test
    @DisplayName("Debe retornar el empleado encontrado por ID")
    void testOpGetEmployeeById_Found() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1000000001)))
                .thenReturn(List.of(mockEmployee));

        clsEmployee result = employeeRepository.opGetEmployeeById(1000000001);

        assertNotNull(result);
        assertEquals(1000000001, result.getAttEmployeeId());
        assertEquals("Carlos Ruiz", result.getAttName());
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(1000000001));
    }

    @Test
    @DisplayName("Debe retornar null cuando no se encuentra el empleado")
    void testOpGetEmployeeById_NotFound() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(999)))
                .thenReturn(Collections.emptyList());

        clsEmployee result = employeeRepository.opGetEmployeeById(999);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta por ID")
    void testOpGetEmployeeById_ThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> employeeRepository.opGetEmployeeById(1000000001));
    }

    @Test
    @DisplayName("Debe listar todos los empleados")
    void testOpGetAllEmployees_ReturnsList() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(mockEmployee));

        List<clsEmployee> result = employeeRepository.opGetAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Carlos Ruiz", result.get(0).getAttName());
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta de todos los empleados")
    void testOpGetAllEmployees_ThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> employeeRepository.opGetAllEmployees());
    }

    @Test
    @DisplayName("Debe mapear correctamente un ResultSet a clsEmployee vía RowMapper")
    void testOpRowToEmployee_RowMapperMapping() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("EMP_ID")).thenReturn(1000000001);
        when(resultSet.getString("EMP_NAME")).thenReturn("Carlos Ruiz");
        when(resultSet.getString("EMP_PHONE")).thenReturn("3001234567");
        when(resultSet.getDouble("EMP_SALARY")).thenReturn(2500000.0);
        when(resultSet.getDate("EMP_HIRE_DATE")).thenReturn(Date.valueOf("2023-05-15"));
        when(resultSet.getString("EMP_ROLE")).thenReturn("seller");
        when(resultSet.getString("EMP_STATE")).thenReturn("active");

        doAnswer(invocation -> {
            RowMapper<clsEmployee> mapper = invocation.getArgument(1);
            clsEmployee mappedEmployee = mapper.mapRow(resultSet, 1);
            return List.of(mappedEmployee);
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class));

        List<clsEmployee> result = employeeRepository.opGetAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1000000001, result.get(0).getAttEmployeeId());
        assertEquals("Carlos Ruiz", result.get(0).getAttName());
        assertEquals("3001234567", result.get(0).getAttPhone());
        assertEquals(2500000.0, result.get(0).getAttSalary());
        assertEquals(Date.valueOf("2023-05-15"), result.get(0).getAttHireDate());
        assertEquals("seller", result.get(0).getAttRole());
        assertEquals("active", result.get(0).getAttState());
    }
}
