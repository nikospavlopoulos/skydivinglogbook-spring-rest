package com.nikospavlopoulos.skydivingrest.rest.error;

import com.nikospavlopoulos.skydivingrest.core.exceptions.*;
import com.nikospavlopoulos.skydivingrest.dto.error.ApiErrorResponseDTO;
import com.nikospavlopoulos.skydivingrest.dto.error.FieldErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test covering the ErrorHandler class.
 * Focusing mainly on pure Junit testing using Mockito
 */

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler; // SUT: Subject under test

    @Mock
    private Logger logger; // Collaborator Slf4j Logger

    @Mock
    private HttpServletRequest request; // Collaborator HttpServletRequest


    // Test - Internal Server Exception
    @Test
    void handleInternalServerException_returns500Response() {
        InternalServerException exception = new InternalServerException("Internal Server Error", "500");

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleInternalServerException(exception, request);

        // Asserts

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Internal Server Error", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }

    // Test - Invalid Argument Exception -

    @Test
    void handleInvalidArgumentException_returns400Response() {
        InvalidArgumentException exception = new InvalidArgumentException(
                "Bad Request", "400"
        );

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleInvalidArgumentException(exception, request);

        // Asserts

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Bad Request", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }

    @Test
    void handleResourceConflictException_returns409Response() {
        ResourceConflictException exception = new ResourceConflictException(
                "Conflict", "409"
        );

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleResourceConflictException(exception, request);

        // Asserts

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Conflict", response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("409", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }

    @Test
    void handleResourceNotFoundException_returns404Response() {
        ResourceNotFoundException exception = new ResourceNotFoundException(
                "Not Found", "404"
        );

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleResourceNotFoundException(exception, request);

        // Asserts

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Not Found", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }

    @Test
    void handleUnauthorizedException_returns401Response() {
        UnauthorizedException exception = new UnauthorizedException(
                "Unauthorized", "401"
        );

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleUnauthorizedException(exception, request);

        // Asserts

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Unauthorized", response.getBody().getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("401", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }
    
    @Test
    void handleValidationException_returns400WithFieldErrors() {
        List<FieldErrorDTO> fieldErrors = List.of(
                new FieldErrorDTO(
                "test",
                "@#",
                "TestMessage"
        ));

        ValidationException exception = new ValidationException(
                "Bad Request", "400", fieldErrors
        );

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleValidationException(exception, request);

        // Asserts

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Bad Request", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().size() == 1);
    }
    
    @Test
    void handleGenericException_returns500Response() {
        GenericException exception = new GenericException(
                "Internal Server Error", "500"
        );

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiErrorResponseDTO> response = errorHandler.handleGenericException(exception, request);

        // Asserts

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Internal Server Error", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }
}
