package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 500 Internal Server Error - Catch-all for unexpected failures inside the server

public class InternalServerException extends GenericException {

    public InternalServerException(String message, String errorCode) {
        super(message, errorCode);
    }
}
