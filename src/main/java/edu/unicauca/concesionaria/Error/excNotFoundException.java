package edu.unicauca.concesionaria.Error;

public class excNotFoundException extends RuntimeException {
    public excNotFoundException(String prmMessage) {
        super(prmMessage);
    }

    public excNotFoundException(String prmMessage, Throwable cause) {
        super(prmMessage, cause);
    }
}
