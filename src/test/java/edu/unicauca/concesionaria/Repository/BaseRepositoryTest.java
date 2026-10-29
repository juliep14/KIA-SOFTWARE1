package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseRepositoryTest {

    // Subclase simple para exponer métodos protegidos estáticos de BaseRepository
    private static class TestRepository extends BaseRepository {
        public static String extract(Exception e) {
            return opExtractMessage(e);
        }

        public static excDatabaseException wrap(Exception e) {
            return opWrap(e);
        }
    }

    @Test
    @DisplayName("Debe extraer el mensaje ORA- delimitado por salto de línea")
    void testOpExtractMessage_OraWithNewline() {
        Exception ex = new Exception("Pre-message\nORA-00001: unique constraint violated\nPost-message");
        String message = TestRepository.extract(ex);

        assertEquals("ORA-00001: unique constraint violated", message);
    }

    @Test
    @DisplayName("Debe extraer el mensaje ORA- sin salto de línea")
    void testOpExtractMessage_OraWithoutNewline() {
        Exception ex = new Exception("Error general: ORA-01400: cannot insert NULL");
        String message = TestRepository.extract(ex);

        assertEquals("ORA-01400: cannot insert NULL", message);
    }

    @Test
    @DisplayName("Debe extraer mensaje estándar cuando no contiene código ORA-")
    void testOpExtractMessage_StandardMessage() {
        Exception ex = new Exception("  Error de conexión a base de datos  ");
        String message = TestRepository.extract(ex);

        assertEquals("Error de conexión a base de datos", message);
    }

    @Test
    @DisplayName("Debe buscar el mensaje en la causa cuando la excepción superior no tiene mensaje")
    void testOpExtractMessage_FromCause() {
        Throwable cause = new RuntimeException("ORA-00942: table or view does not exist");
        Exception ex = new Exception(null, cause);

        String message = TestRepository.extract(ex);
        assertEquals("ORA-00942: table or view does not exist", message);
    }

    @Test
    @DisplayName("Debe retornar toString si no hay mensajes en ninguna causa")
    void testOpExtractMessage_NoMessageInAnyCause() {
        Exception ex = new Exception((String) null);
        String message = TestRepository.extract(ex);

        assertEquals(ex.toString(), message);
    }

    @Test
    @DisplayName("Debe envolver correctamente en excDatabaseException preservando causa y mensaje")
    void testOpWrap() {
        Exception original = new RuntimeException("ORA-00001: unique constraint violated");
        excDatabaseException wrapped = TestRepository.wrap(original);

        assertNotNull(wrapped);
        assertEquals("ORA-00001: unique constraint violated", wrapped.getMessage());
        assertSame(original, wrapped.getCause());
    }
}

