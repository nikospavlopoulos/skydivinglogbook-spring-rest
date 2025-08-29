package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 401 Unauthorized - request requires authentication or has invalid credentials

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GenericException {

    public UnauthorizedException(String message, HttpStatus errorCode) {
        super(message, errorCode);
    }
}
