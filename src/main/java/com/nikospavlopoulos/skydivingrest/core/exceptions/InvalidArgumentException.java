package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 400 Bad Request - client sent invalid parameters in the request.

public class InvalidArgumentException extends GenericException {

        public InvalidArgumentException(String message, String errorCode) {
        super(message, errorCode);
    }

}
