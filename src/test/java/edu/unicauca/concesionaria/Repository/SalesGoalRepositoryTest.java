package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
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
class SalesGoalRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private SalesGoalRepository salesGoalRepository;

    private clsSalesGoal mockGoal;
    private clsEmployee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new clsEmployee();
        mockEmployee.setAttEmployeeId(1000000001);

        mockGoal = new clsSalesGoal();
        mockGoal.setAttSalesGoalId(10);
        mockGoal.setAttEmployee(mockEmployee);
        mockGoal.setAttGoalType("monthly");
        mockGoal.setAttTargetValue(50000000.0);
        mockGoal.setAttStartDate(Date.valueOf("2024-01-01"));
        mockGoal.setAttEndDate(Date.valueOf("2024-01-31"));
        mockGoal.setAttState("active");
    }

    // ==================== REGISTRO DE METAS ====================

    @Test
    @DisplayName("Debe registrar una meta de venta asociada a un empleado exitosamente")
    void testOpRegisterSalesGoal_WithEmployee() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        int resultId = salesGoalRepository.opRegisterSalesGoal(mockGoal);

        assertEquals(10, resultId);
        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq(10),
                eq(1000000001),
                eq("monthly"),
                eq(50000000.0),
                eq(Date.valueOf("2024-01-01")),
                eq(Date.valueOf("2024-01-31")),
                eq("active")
        );
    }

    @Test
    @DisplayName("Debe registrar una meta de venta cuando el empleado asociado es nulo")
    void testOpRegisterSalesGoal_NullEmployee() {
        mockGoal.setAttEmployee(null);
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        int resultId = salesGoalRepository.opRegisterSalesGoal(mockGoal);

        assertEquals(10, resultId);
        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq(10),
                isNull(),
                eq("monthly"),
                eq(50000000.0),
                eq(Date.valueOf("2024-01-01")),
                eq(Date.valueOf("2024-01-31")),
                eq("active")
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla el registro de la meta en BD")
    void testOpRegisterSalesGoal_ThrowsException() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> salesGoalRepository.opRegisterSalesGoal(mockGoal));
    }

    // ==================== ACTUALIZACIÓN DE METAS ====================

    @Test
    @DisplayName("Debe actualizar una meta de venta exitosamente")
    void testOpUpdateSalesGoal_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> salesGoalRepository.opUpdateSalesGoal(mockGoal));

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq(50000000.0),
                eq(Date.valueOf("2024-01-01")),
                eq("monthly"),
                eq(Date.valueOf("2024-01-31")),
                eq("active"),
                eq(10)
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la actualización de la meta")
    void testOpUpdateSalesGoal_ThrowsException() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> salesGoalRepository.opUpdateSalesGoal(mockGoal));
    }

    // ==================== VERIFICACIÓN DE EXISTENCIA ====================

    @Test
    @DisplayName("Debe verificar si la meta existe")
    void testOpSalesGoalExist() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(10)))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(999)))
                .thenReturn(0);

        assertTrue(salesGoalRepository.opSalesGoalExist(10));
        assertFalse(salesGoalRepository.opSalesGoalExist(999));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta de existencia")
    void testOpSalesGoalExist_ThrowsException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenThrow(new DataAccessException("DB timeout") {});

        assertThrows(excDatabaseException.class, () -> salesGoalRepository.opSalesGoalExist(10));
    }

    // ==================== METAS ACTIVAS POR EMPLEADO ====================

    @Test
    @DisplayName("Debe retornar si un empleado tiene metas activas")
    void testOpHasActiveGoal() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1000000001)))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(999)))
                .thenReturn(0);

        assertTrue(salesGoalRepository.opHasActiveGoal(1000000001));
        assertFalse(salesGoalRepository.opHasActiveGoal(999));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta de meta activa")
    void testOpHasActiveGoal_ThrowsException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenThrow(new DataAccessException("DB error") {});

        assertThrows(excDatabaseException.class, () -> salesGoalRepository.opHasActiveGoal(1000000001));
    }

    // ==================== CONSULTA POR ID ====================

    @Test
    @DisplayName("Debe consultar meta por ID y retornar objeto si existe")
    void testOpGetSalesGoalById_Found() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10)))
                .thenReturn(List.of(mockGoal));

        clsSalesGoal result = salesGoalRepository.opGetSalesGoalById(10);

        assertNotNull(result);
        assertEquals(10, result.getAttSalesGoalId());
        assertEquals("monthly", result.getAttGoalType());
    }

    @Test
    @DisplayName("Debe retornar null cuando la meta no existe")
    void testOpGetSalesGoalById_NotFound() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(999)))
                .thenReturn(Collections.emptyList());

        assertNull(salesGoalRepository.opGetSalesGoalById(999));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta por ID")
    void testOpGetSalesGoalById_ThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> salesGoalRepository.opGetSalesGoalById(10));
    }

    // ==================== LISTAR TODAS LAS METAS ====================

    @Test
    @DisplayName("Debe listar todas las metas")
    void testOpGetAllSalesGoals_Success() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(mockGoal));

        List<clsSalesGoal> list = salesGoalRepository.opGetAllSalesGoals();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).getAttSalesGoalId());
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla el listado de metas")
    void testOpGetAllSalesGoals_ThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> salesGoalRepository.opGetAllSalesGoals());
    }

    // ==================== VENTAS LOGRADAS (ACHIEVED SALES) ====================

    @Test
    @DisplayName("Debe retornar ventas logradas dentro del rango de fechas")
    void testOpGetAchievedSales_WithDateRange() {
        Date start = Date.valueOf("2024-01-01");
        Date end = Date.valueOf("2024-01-31");
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(1000000001), eq(start), eq(end)))
                .thenReturn(15000000.0);

        double achieved = salesGoalRepository.opGetAchievedSales(1000000001, start, end);
        assertEquals(15000000.0, achieved);
    }

    @Test
    @DisplayName("Debe retornar ventas acumuladas totales cuando las fechas son nulas")
    void testOpGetAchievedSales_WithoutDateRange() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(1000000001)))
                .thenReturn(25000000.0);

        double achieved = salesGoalRepository.opGetAchievedSales(1000000001, null, null);
        assertEquals(25000000.0, achieved);
    }

    @Test
    @DisplayName("Debe retornar 0.0 cuando queryForObject retorna null")
    void testOpGetAchievedSales_ReturnsZeroWhenNull() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(1000000001)))
                .thenReturn(null);

        double achieved = salesGoalRepository.opGetAchievedSales(1000000001, null, null);
        assertEquals(0.0, achieved);
    }

    @Test
    @DisplayName("Debe retornar 0.0 si ocurre una excepción al consultar ventas")
    void testOpGetAchievedSales_CatchesExceptionAndReturnsZero() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), anyInt()))
                .thenThrow(new RuntimeException("Table does not exist"));

        double achieved = salesGoalRepository.opGetAchievedSales(1000000001, null, null);
        assertEquals(0.0, achieved);
    }

    // ==================== ROWMAPPER ====================

    @Test
    @DisplayName("Debe mapear ResultSet a clsSalesGoal correctamente vía RowMapper con empleado")
    void testOpRowToSalesGoal_RowMapperWithEmployee() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("SGL_ID")).thenReturn(10);
        when(rs.getInt("EMP_ID")).thenReturn(1000000001);
        when(rs.wasNull()).thenReturn(false);
        when(rs.getString("SGL_GOAL_TYPE")).thenReturn("monthly");
        when(rs.getDouble("SGL_TARGET_VALUE")).thenReturn(50000000.0);
        when(rs.getDate("SGL_START_DATE")).thenReturn(Date.valueOf("2024-01-01"));
        when(rs.getDate("SGL_END_DATE")).thenReturn(Date.valueOf("2024-01-31"));
        when(rs.getString("SGL_STATE")).thenReturn("active");

        doAnswer(invocation -> {
            RowMapper<clsSalesGoal> mapper = invocation.getArgument(1);
            clsSalesGoal mapped = mapper.mapRow(rs, 1);
            return List.of(mapped);
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class));

        List<clsSalesGoal> result = salesGoalRepository.opGetAllSalesGoals();

        assertNotNull(result);
        assertEquals(1, result.size());
        clsSalesGoal goal = result.get(0);
        assertEquals(10, goal.getAttSalesGoalId());
        assertNotNull(goal.getAttEmployee());
        assertEquals(1000000001, goal.getAttEmployee().getAttEmployeeId());
        assertEquals("monthly", goal.getAttGoalType());
        assertEquals(50000000.0, goal.getAttTargetValue());
        assertEquals(Date.valueOf("2024-01-01"), goal.getAttStartDate());
        assertEquals(Date.valueOf("2024-01-31"), goal.getAttEndDate());
        assertEquals("active", goal.getAttState());
    }

    @Test
    @DisplayName("Debe mapear ResultSet a clsSalesGoal cuando EMP_ID es nulo en BD (wasNull == true)")
    void testOpRowToSalesGoal_RowMapperNullEmployee() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("SGL_ID")).thenReturn(11);
        when(rs.getInt("EMP_ID")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true);
        when(rs.getString("SGL_GOAL_TYPE")).thenReturn("yearly");
        when(rs.getDouble("SGL_TARGET_VALUE")).thenReturn(200000000.0);
        when(rs.getDate("SGL_START_DATE")).thenReturn(Date.valueOf("2024-01-01"));
        when(rs.getDate("SGL_END_DATE")).thenReturn(Date.valueOf("2024-12-31"));
        when(rs.getString("SGL_STATE")).thenReturn("active");

        doAnswer(invocation -> {
            RowMapper<clsSalesGoal> mapper = invocation.getArgument(1);
            clsSalesGoal mapped = mapper.mapRow(rs, 1);
            return List.of(mapped);
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class));

        List<clsSalesGoal> result = salesGoalRepository.opGetAllSalesGoals();

        assertNotNull(result);
        assertEquals(1, result.size());
        clsSalesGoal goal = result.get(0);
        assertEquals(11, goal.getAttSalesGoalId());
        assertNull(goal.getAttEmployee());
    }
}

