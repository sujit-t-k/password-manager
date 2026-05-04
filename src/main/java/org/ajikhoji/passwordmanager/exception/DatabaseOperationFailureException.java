package org.ajikhoji.passwordmanager.exception;

public class DatabaseOperationFailureException extends RuntimeException {
    public DatabaseOperationFailureException(String message) {
        super(message);
    }
}
