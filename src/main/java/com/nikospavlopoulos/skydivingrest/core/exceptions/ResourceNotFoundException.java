package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 404 Not Found - requested object does not exist

public class ResourceNotFoundException extends GenericException {

    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
