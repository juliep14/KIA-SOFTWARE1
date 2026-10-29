package edu.unicauca.concesionaria.Error;

public class excDatabaseException extends RuntimeException {
    public excDatabaseException(String prmMessage) {
        super(prmMessage);
    }

    public excDatabaseException(String prmMessage, Throwable cause) {
        super(prmMessage, cause);
    }
}
