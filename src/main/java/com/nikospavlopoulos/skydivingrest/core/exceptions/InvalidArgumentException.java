package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 400 Bad Request - client sent invalid parameters in the request.

import org.springframework.http.HttpStatus;

public class InvalidArgumentException extends GenericException {

        public InvalidArgumentException(String message, HttpStatus errorCode) {
        super(message, errorCode);
    }

}
