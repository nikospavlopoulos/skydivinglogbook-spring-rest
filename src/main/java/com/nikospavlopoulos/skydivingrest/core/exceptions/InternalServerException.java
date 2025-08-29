package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 500 Internal Server Error - Catch-all for unexpected failures inside the server

import org.springframework.http.HttpStatus;

public class InternalServerException extends GenericException {

    public InternalServerException(String message, HttpStatus errorCode) {
        super(message, errorCode);
    }
}
