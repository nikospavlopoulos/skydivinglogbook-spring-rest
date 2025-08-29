package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 409 Conflict - resource already exists or is in a conflicting state

import org.apache.coyote.http2.Http2OutputBuffer;
import org.springframework.http.HttpStatus;

public class ResourceConflictException extends GenericException {

    public ResourceConflictException(String message, HttpStatus errorCode) {
        super(message, errorCode);
    }
}
