package com.nikospavlopoulos.skydivingrest.core.exceptions;

public class GenericException extends RuntimeException {

    private final String errorCode;

    public GenericException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
