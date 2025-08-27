package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 409 Conflict - resource already exists or is in a conflicting state

public class ResourceConflictException extends GenericException {

    public ResourceConflictException(String message, String errorCode) {
        super(message, errorCode);
    }
}
