package com.nikospavlopoulos.skydivingrest.core.exceptions;

import org.springframework.http.HttpStatus;

public class GenericException extends RuntimeException {

    private final HttpStatus errorCode;

    public GenericException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }

}
