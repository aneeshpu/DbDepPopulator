package com.aneeshpu.dpdeppop;

import java.sql.SQLException;

public class DBDepPopException extends RuntimeException {
    public DBDepPopException(final String message, final SQLException nestedException) {
        super(message,nestedException);
    }
}
