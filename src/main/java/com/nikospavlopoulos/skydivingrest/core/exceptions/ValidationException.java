package com.nikospavlopoulos.skydivingrest.core.exceptions;

// 400 Bad Request or 422 Unprocessable Entity - request was syntactically correct but failed validation rules

import com.nikospavlopoulos.skydivingrest.dto.error.FieldErrorDTO;

import java.util.List;

public class ValidationException extends GenericException {

    private final List<FieldErrorDTO> fieldErrors;

    public ValidationException(String message, String errorCode, List<FieldErrorDTO> fieldErrors) {
        super(message, errorCode);
        this.fieldErrors = fieldErrors;
    }

    public  List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}
