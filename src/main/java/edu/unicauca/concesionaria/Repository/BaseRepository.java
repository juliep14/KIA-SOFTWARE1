package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Error.excDatabaseException;

public abstract class BaseRepository {

    protected static String opExtractMessage(Exception prmException) {
        Throwable varCurrent = prmException;
        while (varCurrent != null) {
            String varMessage = varCurrent.getMessage();
            if (varMessage != null && !varMessage.isBlank()) {
                int varOraIndex = varMessage.indexOf("ORA-");
                if (varOraIndex >= 0) {
                    int varEnd = varMessage.indexOf('\n', varOraIndex);
                    return varEnd > varOraIndex
                            ? varMessage.substring(varOraIndex, varEnd).trim()
                            : varMessage.substring(varOraIndex).trim();
                }
                return varMessage.trim();
            }
            varCurrent = varCurrent.getCause();
        }
        return prmException.toString();
    }

    protected static excDatabaseException opWrap(Exception prmException) {
        return new excDatabaseException(opExtractMessage(prmException), prmException);
    }
}
