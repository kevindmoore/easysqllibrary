package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Exceptions for Database errors.
 */
public class DBException extends Exception {

    public DBException() {
    }

    public DBException(String detailMessage) {
        super(detailMessage);
    }

    public DBException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DBException(Throwable throwable) {
        super(throwable);
    }
}
