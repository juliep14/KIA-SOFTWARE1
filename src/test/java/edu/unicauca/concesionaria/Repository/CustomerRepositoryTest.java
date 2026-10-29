package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Model.clsCustomer;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CustomerRepository customerRepository;

    private clsCustomer mockCustomer;

    @BeforeEach
    void setUp() {
        mockCustomer = new clsCustomer();
        mockCustomer.setAttCustomerId(1);
        mockCustomer.setAttName("Ana Gómez");
        mockCustomer.setAttPhone("3109876543");
        mockCustomer.setAttEmail("ana@gmail.com");
        mockCustomer.setAttState("active");
    }

    // ==================== REGISTRO DE CLIENTES ====================

    @Test
    @DisplayName("Debe registrar un cliente correctamente en la base de datos")
    void testOpRegisterCustomer_Success() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> customerRepository.opRegisterCustomer(mockCustomer));

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq(1),
                eq("Ana Gómez"),
                eq("3109876543"),
                eq("ana@gmail.com"),
                eq("active")
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla el registro del cliente")
    void testOpRegisterCustomer_ThrowsException() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new DataAccessException("DB error") {});

        assertThrows(excDatabaseException.class, () -> customerRepository.opRegisterCustomer(mockCustomer));
    }

    // ==================== ACTUALIZACIÓN DE CLIENTES ====================

    @Test
    @DisplayName("Debe actualizar un cliente con el mismo ID usando SQL_UPDATE")
    void testOpUpdateCustomer_SameId() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> customerRepository.opUpdateCustomer(1, mockCustomer));

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq("Ana Gómez"),
                eq("3109876543"),
                eq("ana@gmail.com"),
                eq(1)
        );
    }

    @Test
    @DisplayName("Debe actualizar un cliente cambiando el ID y actualizando ventas relacionadas")
    void testOpUpdateCustomer_DifferentId() {
        mockCustomer.setAttCustomerId(2); // Nuevo ID

        when(jdbcTemplate.update(eq("UPDATE TBL_SALE SET CUS_ID = ? WHERE CUS_ID = ?"), eq(2), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), eq(2), eq("Ana Gómez"), eq("3109876543"), eq("ana@gmail.com"), eq(1)))
                .thenReturn(1);

        assertDoesNotThrow(() -> customerRepository.opUpdateCustomer(1, mockCustomer));

        verify(jdbcTemplate, times(1)).update(eq("UPDATE TBL_SALE SET CUS_ID = ? WHERE CUS_ID = ?"), eq(2), eq(1));
    }

    @Test
    @DisplayName("Debe ignorar excepción al actualizar TBL_SALE cuando cambia de ID")
    void testOpUpdateCustomer_DifferentIdIgnoresSaleUpdateException() {
        mockCustomer.setAttCustomerId(2);

        when(jdbcTemplate.update(eq("UPDATE TBL_SALE SET CUS_ID = ? WHERE CUS_ID = ?"), eq(2), eq(1)))
                .thenThrow(new RuntimeException("Table not found"));
        when(jdbcTemplate.update(anyString(), eq(2), eq("Ana Gómez"), eq("3109876543"), eq("ana@gmail.com"), eq(1)))
                .thenReturn(1);

        assertDoesNotThrow(() -> customerRepository.opUpdateCustomer(1, mockCustomer));
    }

    @Test
    @DisplayName("Debe delegar opUpdateCustomer(customer) hacia opUpdateCustomer(id, customer)")
    void testOpUpdateCustomer_OverloadDelegation() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> customerRepository.opUpdateCustomer(mockCustomer));

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq("Ana Gómez"),
                eq("3109876543"),
                eq("ana@gmail.com"),
                eq(1)
        );
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la actualización del cliente")
    void testOpUpdateCustomer_ThrowsException() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> customerRepository.opUpdateCustomer(mockCustomer));
    }

    // ==================== DESHABILITAR CLIENTE ====================

    @Test
    @DisplayName("Debe deshabilitar un cliente (cambiar estado a inactive)")
    void testOpDisableCustomer_Success() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);

        assertDoesNotThrow(() -> customerRepository.opDisableCustomer(1));
        verify(jdbcTemplate, times(1)).update(anyString(), eq(1));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la deshabilitación")
    void testOpDisableCustomer_ThrowsException() {
        when(jdbcTemplate.update(anyString(), eq(1)))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> customerRepository.opDisableCustomer(1));
    }

    // ==================== VERIFICACIÓN DE EXISTENCIA ====================

    @Test
    @DisplayName("Debe retornar true si el cliente existe")
    void testOpCustomerExist_ReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);

        boolean exists = customerRepository.opCustomerExist(1);
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false si el cliente no existe o count es null")
    void testOpCustomerExist_ReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(999)))
                .thenReturn(0);
        assertFalse(customerRepository.opCustomerExist(999));

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(888)))
                .thenReturn(null);
        assertFalse(customerRepository.opCustomerExist(888));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta de existencia")
    void testOpCustomerExist_ThrowsException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenThrow(new DataAccessException("DB timeout") {});

        assertThrows(excDatabaseException.class, () -> customerRepository.opCustomerExist(1));
    }

    // ==================== CONSULTA POR ID ====================

    @Test
    @DisplayName("Debe retornar el cliente encontrado por ID")
    void testOpGetCustomerById_Found() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(List.of(mockCustomer));

        clsCustomer result = customerRepository.opGetCustomerById(1);

        assertNotNull(result);
        assertEquals(1, result.getAttCustomerId());
        assertEquals("Ana Gómez", result.getAttName());
    }

    @Test
    @DisplayName("Debe retornar null cuando el cliente no existe")
    void testOpGetCustomerById_NotFound() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(999)))
                .thenReturn(Collections.emptyList());

        clsCustomer result = customerRepository.opGetCustomerById(999);
        assertNull(result);
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta por ID")
    void testOpGetCustomerById_ThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> customerRepository.opGetCustomerById(1));
    }

    // ==================== LISTAR TODOS LOS CLIENTES ====================

    @Test
    @DisplayName("Debe listar todos los clientes")
    void testOpGetAllCustomers_ReturnsList() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(mockCustomer));

        List<clsCustomer> list = customerRepository.opGetAllCustomers();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Ana Gómez", list.get(0).getAttName());
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la consulta general de clientes")
    void testOpGetAllCustomers_ThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new DataAccessException("DB failure") {});

        assertThrows(excDatabaseException.class, () -> customerRepository.opGetAllCustomers());
    }

    // ==================== ROWMAPPER ====================

    @Test
    @DisplayName("Debe mapear ResultSet a clsCustomer correctamente mediante RowMapper")
    void testOpRowToCustomer_RowMapperMapping() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("CUS_ID")).thenReturn(1);
        when(rs.getString("CUS_NAME")).thenReturn("Ana Gómez");
        when(rs.getString("CUS_PHONE")).thenReturn("3109876543");
        when(rs.getString("CUS_EMAIL")).thenReturn("ana@gmail.com");
        when(rs.getString("CUS_STATE")).thenReturn("active");

        doAnswer(invocation -> {
            RowMapper<clsCustomer> mapper = invocation.getArgument(1);
            clsCustomer mapped = mapper.mapRow(rs, 1);
            return List.of(mapped);
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class));

        List<clsCustomer> result = customerRepository.opGetAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getAttCustomerId());
        assertEquals("Ana Gómez", result.get(0).getAttName());
        assertEquals("3109876543", result.get(0).getAttPhone());
        assertEquals("ana@gmail.com", result.get(0).getAttEmail());
        assertEquals("active", result.get(0).getAttState());
    }
}

