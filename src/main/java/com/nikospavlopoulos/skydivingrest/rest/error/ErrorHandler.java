package com.nikospavlopoulos.skydivingrest.rest.error;

import com.nikospavlopoulos.skydivingrest.core.exceptions.*;
import com.nikospavlopoulos.skydivingrest.dto.error.ApiErrorResponseDTO;
import com.nikospavlopoulos.skydivingrest.dto.error.FieldErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * ErrorHandler maps exceptions to HTTP responses
 */

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    // Handle - InternalServerException = handleInternalServerException
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiErrorResponseDTO>
                handleInternalServerException(InternalServerException exception, HttpServletRequest request) {

        logException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString());

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                       null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle - InvalidArgumentException = handleInvalidArgumentException
    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO>
                    handleInvalidArgumentException(InvalidArgumentException exception, HttpServletRequest request) {

        logException(
                HttpStatus.BAD_REQUEST,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString());

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                        null),
                HttpStatus.BAD_REQUEST);
    }

    // Handle - ResourceConflictException = handleResourceConflictException

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponseDTO>
                    handleResourceConflictException(ResourceConflictException exception, HttpServletRequest request) {

        logException(
                HttpStatus.CONFLICT,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString()
        );

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.CONFLICT,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                        null),
                HttpStatus.CONFLICT);
    }


    // Handle - ResourceNotFoundException = handleResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO>
                    handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {

        logException(
                HttpStatus.NOT_FOUND,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString()
        );

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                        null),
                HttpStatus.NOT_FOUND
                );
    }

    // Handle - UnauthorizedException = handleUnauthorizedException
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponseDTO>
                    handleUnauthorizedException(UnauthorizedException exception, HttpServletRequest request) {

        logException(
                HttpStatus.UNAUTHORIZED,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString());

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.UNAUTHORIZED,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                        null),
                HttpStatus.UNAUTHORIZED
                );
    }

    // Handle - ValidationException = handleValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponseDTO>
                    handleValidationException(ValidationException exception, HttpServletRequest request) {


        logException(
                HttpStatus.BAD_REQUEST,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString()
        );

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                        exception.getFieldErrors()),
                HttpStatus.BAD_REQUEST);
    }

    /*
    // TODO: Handle AccessDeniedException
    Ensure global ErrorHandler maps that exception to a 403 - Forbidden JSON response.
     */

    // Handle - Generic Exception = handleGenericException

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiErrorResponseDTO>
    handleGenericException(GenericException exception, HttpServletRequest request) {


        logException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception,
                exception.getMessage(),
                request,
                exception.getErrorCode().toString()
        );

        return new ResponseEntity<>(
                createErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage(),
                        exception.getErrorCode().toString(),
                        request.getRequestURI(),
                        null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ///  Helpers

    // Creating the Error Message
    public ApiErrorResponseDTO createErrorResponse(HttpStatus status, String message, String code, String path, List<FieldErrorDTO> fieldErrors) {
        return ApiErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .code(code)
                .path(path)
                .fieldErrors(fieldErrors != null ? fieldErrors : Collections.emptyList())
                .build();
    }

    // Centralizing the logging for consistency
    public void logException(HttpStatus status, Throwable exception, String message, HttpServletRequest request, String code) {

        // Message template "{HTTP_METHOD} {REQUEST_URI} -> {HTTP_STATUS} (code = {}) - {message}"

        if (status.value() >= 500) {
            log.error("{} {} -> {} (code = {}) - {}",request.getMethod(), request.getRequestURI(), status.value(), code, message, exception);
        } else if (status.value() == 404) {
            log.info("{} {} -> {} (code = {}) - {}", request.getMethod(), request.getRequestURI(), status.value(), code, message);
        } else {
            log.warn("{} {} -> {} (code = {}) - {}", request.getMethod(), request.getRequestURI(), status.value(), code, message);
        }

    }

}
