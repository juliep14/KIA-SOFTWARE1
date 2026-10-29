package edu.unicauca.concesionaria.Error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    @DisplayName("Debe verificar constructores de excValidationException")
    void testExcValidationException() {
        excValidationException ex1 = new excValidationException("Error de validación");
        assertEquals("Error de validación", ex1.getMessage());

        Throwable cause = new IllegalArgumentException("Causa raíz");
        excValidationException ex2 = new excValidationException("Error con causa", cause);
        assertEquals("Error con causa", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Debe verificar constructores de excNotFoundException")
    void testExcNotFoundException() {
        excNotFoundException ex1 = new excNotFoundException("Recurso no encontrado");
        assertEquals("Recurso no encontrado", ex1.getMessage());

        Throwable cause = new RuntimeException("Causa raíz");
        excNotFoundException ex2 = new excNotFoundException("No encontrado con causa", cause);
        assertEquals("No encontrado con causa", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Debe verificar constructores de excDuplicateDataException")
    void testExcDuplicateDataException() {
        excDuplicateDataException ex1 = new excDuplicateDataException("Dato duplicado");
        assertEquals("Dato duplicado", ex1.getMessage());

        Throwable cause = new RuntimeException("Causa raíz");
        excDuplicateDataException ex2 = new excDuplicateDataException("Duplicado con causa", cause);
        assertEquals("Duplicado con causa", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Debe verificar constructores de excDatabaseException")
    void testExcDatabaseException() {
        excDatabaseException ex1 = new excDatabaseException("Error de base de datos");
        assertEquals("Error de base de datos", ex1.getMessage());

        Throwable cause = new RuntimeException("Causa raíz");
        excDatabaseException ex2 = new excDatabaseException("DB con causa", cause);
        assertEquals("DB con causa", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }
}

