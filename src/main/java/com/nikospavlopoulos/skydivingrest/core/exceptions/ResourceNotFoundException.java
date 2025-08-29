package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 404 Not Found - requested object does not exist

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends GenericException {

    public ResourceNotFoundException(String message, HttpStatus errorCode) {
        super(message, errorCode);
    }
}
