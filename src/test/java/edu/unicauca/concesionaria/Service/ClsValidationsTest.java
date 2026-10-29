package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClsValidationsTest {

    // --- PRUEBAS DE TELÉFONO ---

    @Test
    @DisplayName("Debe validar un teléfono correcto de 10 dígitos")
    void testOpValidatePhone_Valid() {
        assertDoesNotThrow(() -> clsValidations.opValidatePhone("3001234567"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "30012345678", "abcdefghij", "", "300 123456"})
    @DisplayName("Debe lanzar excepción para teléfonos inválidos")
    void testOpValidatePhone_Invalid(String phone) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidatePhone(phone));
    }

    @Test
    void testOpValidatePhone_Null() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidatePhone(null));
    }

    // --- PRUEBAS DE EMAIL ---

    @ParameterizedTest
    @ValueSource(strings = {"user@domain.com", "test.user+1@sub.domain.co"})
    @DisplayName("Debe aceptar correos con formato válido")
    void testOpValidateEmail_Valid(String email) {
        assertDoesNotThrow(() -> clsValidations.opValidateEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@.com", "user@domain", "@domain.com"})
    @DisplayName("Debe rechazar correos con formato incorrecto")
    void testOpValidateEmail_InvalidFormat(String email) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateEmail(email));
    }

    @Test
    @DisplayName("Debe rechazar correo de más de 50 caracteres")
    void testOpValidateEmail_TooLong() {
        String longEmail = "a".repeat(42) + "@test.com"; // 51 caracteres
        assertThrows(excValidationException.class, () -> clsValidations.opValidateEmail(longEmail));
    }

    @Test
    @DisplayName("Debe permitir email nulo (opcional)")
    void testOpValidateEmail_NullAllowed() {
        assertDoesNotThrow(() -> clsValidations.opValidateEmail(null));
    }

    // --- PRUEBAS DE ESTADO ---

    @ParameterizedTest
    @ValueSource(strings = {"active", "inactive", "ACTIVE", " Inactive "})
    void testOpValidateState_Valid(String state) {
        assertDoesNotThrow(() -> clsValidations.opValidateState(state));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pending", "deleted", "", "   "})
    void testOpValidateState_Invalid(String state) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateState(state));
    }

    // --- PRUEBAS DE FECHA ---

    @Test
    void testOpValidateDate_TodayAndPastValid() {
        Date today = Date.valueOf(LocalDate.now());
        Date past = Date.valueOf(LocalDate.now().minusDays(10));

        assertDoesNotThrow(() -> clsValidations.opValidateDate(today));
        assertDoesNotThrow(() -> clsValidations.opValidateDate(past));
    }

    @Test
    void testOpValidateDate_FutureAndNullInvalid() {
        Date future = Date.valueOf(LocalDate.now().plusDays(5));

        assertThrows(excValidationException.class, () -> clsValidations.opValidateDate(future));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateDate(null));
    }

    // --- PRUEBAS DE NOMBRE ---

    @Test
    void testOpValidateName_Valid() {
        assertDoesNotThrow(() -> clsValidations.opValidateName("Juan Pérez", 3, 50));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan123", "Carlos_Gomez", "Ana#Maria", "", "   "})
    void testOpValidateName_InvalidCharactersOrBlank(String name) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateName(name, 3, 50));
    }

    @Test
    void testOpValidateName_OutOfRange() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateName("Al", 3, 50)); // Muy corto
        assertThrows(excValidationException.class, () -> clsValidations.opValidateName("A".repeat(51), 3, 50)); // Muy largo
    }

    // --- PRUEBAS DE SALARIO Y NÚMEROS POSITIVOS ---

    @Test
    void testOpValidateSalary_Valid() {
        assertDoesNotThrow(() -> clsValidations.opValidateSalary(2500000.0));
    }

    @Test
    void testOpValidateSalary_ExceedsLimitOrNegative() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateSalary(-100.0));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateSalary(100_000_000.0));
    }

    // --- PRUEBAS DE ROL ---

    @ParameterizedTest
    @ValueSource(strings = {"seller", "manager", "SELLER", " Manager "})
    void testOpValidateRole_Valid(String role) {
        assertDoesNotThrow(() -> clsValidations.opValidateRole(role));
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "supervisor", "", "   "})
    void testOpValidateRole_Invalid(String role) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateRole(role));
    }

    // --- PRUEBAS DE ID ---

    @Test
    void testOpValidateId_PrimitiveValidAndInvalid() {
        assertDoesNotThrow(() -> clsValidations.opValidateId(1));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateId(0));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateId(-5));
    }

    @Test
    void testOpValidateId_WrapperInteger() {
        assertDoesNotThrow(() -> clsValidations.opValidateId(Integer.valueOf(10)));
        assertDoesNotThrow(() -> clsValidations.opValidateId((Integer) null)); // Permite nulo según la firma
        assertThrows(excValidationException.class, () -> clsValidations.opValidateId(Integer.valueOf(0)));
    }

    // --- PRUEBAS DE RANGO DE FECHAS ---

    @Test
    void testOpValidateDateRange_Valid() {
        Date start = Date.valueOf(LocalDate.now());
        Date end = Date.valueOf(LocalDate.now().plusDays(30));

        assertDoesNotThrow(() -> clsValidations.opValidateDateRange(start, end));
        assertDoesNotThrow(() -> clsValidations.opValidateDateRange(start, start)); // Mismo día
    }

    @Test
    void testOpValidateDateRange_EndBeforeStart() {
        Date start = Date.valueOf(LocalDate.now());
        Date end = Date.valueOf(LocalDate.now().minusDays(1));

        assertThrows(excValidationException.class, () -> clsValidations.opValidateDateRange(start, end));
    }

    @Test
    void testOpValidateDateRange_NullDates() {
        Date today = Date.valueOf(LocalDate.now());
        // Cuando alguna fecha es nula, no debe lanzar excepción de rango
        assertDoesNotThrow(() -> clsValidations.opValidateDateRange(null, today));
        assertDoesNotThrow(() -> clsValidations.opValidateDateRange(today, null));
        assertDoesNotThrow(() -> clsValidations.opValidateDateRange(null, null));
    }

    // --- PRUEBAS DE TIPO DE META (GOAL TYPE) ---

    @ParameterizedTest
    @ValueSource(strings = {"monthly", "quarterly", "yearly", "MONTHLY", " Quarterly ", "YEARLY"})
    @DisplayName("Debe aceptar tipos de meta válidos")
    void testOpValidateGoalType_Valid(String goalType) {
        assertDoesNotThrow(() -> clsValidations.opValidateGoalType(goalType));
    }

    @ParameterizedTest
    @ValueSource(strings = {"daily", "weekly", "biannual", "invalid", "", "   "})
    @DisplayName("Debe rechazar tipos de meta no permitidos o vacíos")
    void testOpValidateGoalType_Invalid(String goalType) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateGoalType(goalType));
    }

    @Test
    @DisplayName("Debe rechazar tipo de meta nulo")
    void testOpValidateGoalType_Null() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateGoalType(null));
    }

    // --- PRUEBAS DE ESTADO DE META (SALES GOAL STATE) ---

    @ParameterizedTest
    @ValueSource(strings = {"inactive", "active", "complete", "INACTIVE", " Active ", "COMPLETE"})
    @DisplayName("Debe aceptar estados de meta válidos")
    void testOpValidateSalesGoalState_Valid(String state) {
        assertDoesNotThrow(() -> clsValidations.opValidateSalesGoalState(state));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pending", "cancelled", "finished", "", "   "})
    @DisplayName("Debe rechazar estados de meta inválidos")
    void testOpValidateSalesGoalState_Invalid(String state) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateSalesGoalState(state));
    }

    @Test
    @DisplayName("Debe rechazar estado de meta nulo")
    void testOpValidateSalesGoalState_Null() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateSalesGoalState(null));
    }

    // --- PRUEBAS DE VALOR DE META (TARGET VALUE) ---

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 1000.0, 50_000_000.0, 999_999_999_999.0})
    @DisplayName("Debe aceptar valores de meta válidos")
    void testOpValidateTargetValue_Valid(double value) {
        assertDoesNotThrow(() -> clsValidations.opValidateTargetValue(value));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -5000.0, 1_000_000_000_000.0})
    @DisplayName("Debe rechazar valores de meta cero, negativos o fuera del límite")
    void testOpValidateTargetValue_Invalid(double value) {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateTargetValue(value));
    }

    // --- PRUEBAS DE NÚMERO POSITIVO (POSITIVE NUMBER) ---

    @Test
    @DisplayName("Debe validar números mayores o iguales a cero")
    void testOpValidatePositiveNumber() {
        assertDoesNotThrow(() -> clsValidations.opValidatePositiveNumber(0.0, "Campo"));
        assertDoesNotThrow(() -> clsValidations.opValidatePositiveNumber(100.5, "Campo"));
        assertThrows(excValidationException.class, () -> clsValidations.opValidatePositiveNumber(-0.01, "Campo"));
    }

    // --- PRUEBAS ADICIONALES DE NULOS EN ESTADO, ROL Y NOMBRE ---

    @Test
    void testOpValidateState_NullOrBlank() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateState(null));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateState(""));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateState("   "));
    }

    @Test
    void testOpValidateRole_NullOrBlank() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateRole(null));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateRole(""));
        assertThrows(excValidationException.class, () -> clsValidations.opValidateRole("   "));
    }

    @Test
    void testOpValidateName_Null() {
        assertThrows(excValidationException.class, () -> clsValidations.opValidateName(null, 3, 50));
    }
}