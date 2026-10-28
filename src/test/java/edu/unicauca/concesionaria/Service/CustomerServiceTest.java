package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Error.excValidationException;
import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.Repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

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
    @DisplayName("Debe registrar un cliente exitosamente con todos sus campos")
    void testOpRegisterCustomer_Success() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(false);
        when(customerRepository.opGetCustomerById(id)).thenReturn(mockCustomer);

        assertDoesNotThrow(() -> customerService.opRegisterCustomer(
                id, "Ana Gómez", "ana@gmail.com", "3109876543", "active"
        ));

        verify(customerRepository, times(1)).opCustomerExist(id);
        verify(customerRepository, times(1)).opRegisterCustomer(any(clsCustomer.class));
        verify(customerRepository, times(1)).opGetCustomerById(id);
    }

    @Test
    @DisplayName("Debe registrar un cliente con email nulo o en blanco y estado por defecto 'active'")
    void testOpRegisterCustomer_DefaultStateAndNullEmail() {
        int id = 2;
        clsCustomer customerWithoutEmail = new clsCustomer();
        customerWithoutEmail.setAttCustomerId(id);
        customerWithoutEmail.setAttName("Pedro Perez");
        customerWithoutEmail.setAttPhone("3201234567");
        customerWithoutEmail.setAttEmail(null);
        customerWithoutEmail.setAttState("active");

        when(customerRepository.opCustomerExist(id)).thenReturn(false);
        when(customerRepository.opGetCustomerById(id)).thenReturn(customerWithoutEmail);

        assertDoesNotThrow(() -> customerService.opRegisterCustomer(
                id, "Pedro Perez", null, "3201234567", null
        ));

        verify(customerRepository, times(1)).opRegisterCustomer(argThat(c ->
                c.getAttEmail() == null && "active".equals(c.getAttState())
        ));
    }

    @Test
    @DisplayName("Debe lanzar excDuplicateDataException si el cliente ya existe")
    void testOpRegisterCustomer_DuplicateId() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(true);

        assertThrows(excDuplicateDataException.class, () ->
                customerService.opRegisterCustomer(id, "Ana Gómez", "ana@gmail.com", "3109876543", "active")
        );

        verify(customerRepository, never()).opRegisterCustomer(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si el ID es menor o igual a cero")
    void testOpRegisterCustomer_InvalidId() {
        assertThrows(excValidationException.class, () ->
                customerService.opRegisterCustomer(0, "Ana Gómez", "ana@gmail.com", "3109876543", "active")
        );
        assertThrows(excValidationException.class, () ->
                customerService.opRegisterCustomer(-5, "Ana Gómez", "ana@gmail.com", "3109876543", "active")
        );
    }

    @Test
    @DisplayName("Debe lanzar excValidationException cuando algún campo no cumple con las validaciones")
    void testOpRegisterCustomer_ValidationFailures() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(false);

        // Nombre inválido
        assertThrows(excValidationException.class, () ->
                customerService.opRegisterCustomer(id, "An", "ana@gmail.com", "3109876543", "active"));

        // Teléfono inválido
        assertThrows(excValidationException.class, () ->
                customerService.opRegisterCustomer(id, "Ana Gomez", "ana@gmail.com", "31098", "active"));

        // Email inválido
        assertThrows(excValidationException.class, () ->
                customerService.opRegisterCustomer(id, "Ana Gomez", "correo-invalido", "3109876543", "active"));

        // Estado inválido
        assertThrows(excValidationException.class, () ->
                customerService.opRegisterCustomer(id, "Ana Gomez", "ana@gmail.com", "3109876543", "invalid"));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si el repositorio falla al registrar")
    void testOpRegisterCustomer_DatabaseException() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(false);
        doThrow(new excDatabaseException("DB failure")).when(customerRepository).opRegisterCustomer(any());

        assertThrows(excDatabaseException.class, () ->
                customerService.opRegisterCustomer(id, "Ana Gomez", "ana@gmail.com", "3109876543", "active")
        );
    }

    // ==================== ACTUALIZACIÓN DE CLIENTES ====================

    @Test
    @DisplayName("Debe actualizar un cliente exitosamente")
    void testOpUpdateCustomer_Success() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(true);
        when(customerRepository.opGetCustomerById(id)).thenReturn(mockCustomer);

        assertDoesNotThrow(() -> customerService.opUpdateCustomer(
                id, "Ana Gomez Modificada", "3151234567", "nuevo@gmail.com", "inactive"
        ));

        verify(customerRepository, times(1)).opUpdateCustomer(any(clsCustomer.class));
    }

    @Test
    @DisplayName("Debe actualizar un cliente asignando null si el correo viene en blanco")
    void testOpUpdateCustomer_BlankEmailSetsNull() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(true);
        when(customerRepository.opGetCustomerById(id)).thenReturn(mockCustomer);

        assertDoesNotThrow(() -> customerService.opUpdateCustomer(
                id, "Ana Gomez", "3151234567", "", "active"
        ));

        verify(customerRepository, times(1)).opUpdateCustomer(argThat(c -> c.getAttEmail() == null));
    }

    @Test
    @DisplayName("Debe lanzar excNotFoundException al actualizar un cliente inexistente")
    void testOpUpdateCustomer_NotFound() {
        int id = 999;
        when(customerRepository.opCustomerExist(id)).thenReturn(false);

        assertThrows(excNotFoundException.class, () ->
                customerService.opUpdateCustomer(id, "Ana Gomez", "3151234567", "ana@gmail.com", "active")
        );

        verify(customerRepository, never()).opUpdateCustomer(any());
    }

    @Test
    @DisplayName("Debe lanzar excValidationException al actualizar con ID inválido")
    void testOpUpdateCustomer_InvalidId() {
        assertThrows(excValidationException.class, () ->
                customerService.opUpdateCustomer(0, "Ana Gomez", "3151234567", "ana@gmail.com", "active")
        );
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si los datos a actualizar son inválidos")
    void testOpUpdateCustomer_InvalidData() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(true);

        assertThrows(excValidationException.class, () ->
                customerService.opUpdateCustomer(id, "A", "3151234567", "ana@gmail.com", "active"));
        assertThrows(excValidationException.class, () ->
                customerService.opUpdateCustomer(id, "Ana Gomez", "123", "ana@gmail.com", "active"));
        assertThrows(excValidationException.class, () ->
                customerService.opUpdateCustomer(id, "Ana Gomez", "3151234567", "invalido", "active"));
        assertThrows(excValidationException.class, () ->
                customerService.opUpdateCustomer(id, "Ana Gomez", "3151234567", "ana@gmail.com", "estado-raro"));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si el repositorio falla al actualizar")
    void testOpUpdateCustomer_DatabaseException() {
        int id = 1;
        when(customerRepository.opCustomerExist(id)).thenReturn(true);
        when(customerRepository.opGetCustomerById(id)).thenReturn(mockCustomer);
        doThrow(new excDatabaseException("DB error")).when(customerRepository).opUpdateCustomer(any());

        assertThrows(excDatabaseException.class, () ->
                customerService.opUpdateCustomer(id, "Ana Gomez", "3151234567", "ana@gmail.com", "active")
        );
    }

    // ==================== CONSULTA DE CLIENTES Y CACHÉ ====================

    @Test
    @DisplayName("Debe consultar un cliente por ID y aprovechar el caché en la segunda llamada")
    void testOpGetCustomerById_CacheHit() {
        int id = 1;
        when(customerRepository.opGetCustomerById(id)).thenReturn(mockCustomer);

        clsCustomer firstCall = customerService.opGetCustomerById(id);
        assertNotNull(firstCall);
        assertEquals(id, firstCall.getAttCustomerId());

        clsCustomer secondCall = customerService.opGetCustomerById(id);
        assertSame(firstCall, secondCall);

        verify(customerRepository, times(1)).opGetCustomerById(id);
    }

    @Test
    @DisplayName("Debe lanzar excNotFoundException si el cliente no existe en base de datos")
    void testOpGetCustomerById_NotFound() {
        int id = 999;
        when(customerRepository.opGetCustomerById(id)).thenReturn(null);

        assertThrows(excNotFoundException.class, () -> customerService.opGetCustomerById(id));
    }

    @Test
    @DisplayName("Debe lanzar excValidationException si el ID es menor o igual a cero")
    void testOpGetCustomerById_InvalidId() {
        assertThrows(excValidationException.class, () -> customerService.opGetCustomerById(0));
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la BD al consultar por ID")
    void testOpGetCustomerById_DatabaseException() {
        int id = 1;
        when(customerRepository.opGetCustomerById(id)).thenThrow(new excDatabaseException("DB error"));

        assertThrows(excDatabaseException.class, () -> customerService.opGetCustomerById(id));
    }

    // ==================== LISTAR TODOS LOS CLIENTES ====================

    @Test
    @DisplayName("Debe listar todos los clientes")
    void testOpGetAllCustomers_Success() {
        when(customerRepository.opGetAllCustomers()).thenReturn(List.of(mockCustomer));

        List<clsCustomer> list = customerService.opGetAllCustomers();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Ana Gómez", list.get(0).getAttName());
        verify(customerRepository, times(1)).opGetAllCustomers();
    }

    @Test
    @DisplayName("Debe lanzar excDatabaseException si falla la BD al listar clientes")
    void testOpGetAllCustomers_DatabaseException() {
        when(customerRepository.opGetAllCustomers()).thenThrow(new excDatabaseException("DB error"));

        assertThrows(excDatabaseException.class, () -> customerService.opGetAllCustomers());
    }
}

