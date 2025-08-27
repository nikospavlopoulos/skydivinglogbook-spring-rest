package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 401 Unauthorized - request requires authentication or has invalid credentials

public class UnauthorizedException extends GenericException {

    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode);
    }
}
