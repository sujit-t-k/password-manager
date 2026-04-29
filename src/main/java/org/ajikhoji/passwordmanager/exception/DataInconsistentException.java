package org.ajikhoji.passwordmanager.exception;

public class DataInconsistentException extends RuntimeException {

    public DataInconsistentException(String msg, String data) {
        super(msg + "\nInconsistent Data :" + data);
    }

}
