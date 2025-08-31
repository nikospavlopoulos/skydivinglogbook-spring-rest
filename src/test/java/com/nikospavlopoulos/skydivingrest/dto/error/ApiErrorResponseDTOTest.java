package com.nikospavlopoulos.skydivingrest.dto.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApiErrorResponseDTOTest {

    @Autowired
    private ObjectMapper objectMapper;


    // Test Builder - object creation
    @Test
    void apiErrorResponseBuilderSuccess() {

        FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(
                "test",
                "@#",
                "TestMessage"
        );

        List<FieldErrorDTO> listOfFieldErrors = Arrays.asList(fieldErrorDTO);

        LocalDateTime now = LocalDateTime.now();

        ApiErrorResponseDTO dto = ApiErrorResponseDTO.builder()
                .timestamp(now)
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation Failed")
                .code("400")
                .path("api/users")
                .fieldErrors(listOfFieldErrors)
                .build();

        assertEquals(now, dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("Validation Failed", dto.getMessage());
        assertEquals("400", dto.getCode());
        assertEquals("api/users", dto.getPath());
        assertTrue(listOfFieldErrors.size() == 1);
    }


    //Test - DTO correctly serializes to JSON
    @Test
    void apiErrorDto_whenSerialized_thenTimestampIsIsoAndFieldErrorsPresent() throws JsonProcessingException {
        FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(
                "test",
                "@#",
                "TestMessage"
        );

        List<FieldErrorDTO> listOfFieldErrors = Arrays.asList(fieldErrorDTO);

        LocalDateTime time = LocalDateTime.of(2025,8,27,12,0);

        ApiErrorResponseDTO dto = ApiErrorResponseDTO.builder()
                .timestamp(time)
                .status(400)
                .error("Bad Request")
                .code("400")
                .message("Validation Failed")
                .path("/api/users")
                .fieldErrors(listOfFieldErrors)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        Map<String, Object> map = objectMapper.readValue(json, Map.class);

        assertEquals("2025-08-27T12:00:00", map.get("timestamp"));
        assertEquals(400, map.get("status"));
        assertEquals("Bad Request", map.get("error"));
        assertEquals("Validation Failed", map.get("message"));
        assertEquals("400", map.get("code"));
        assertEquals("/api/users", map.get("path"));
        assertTrue(json.contains("\"field\":\"test\""));
        assertTrue(json.contains("\"message\":\"TestMessage\""));
    }

    //Test - JSON correctly deserializes to DTO

    @Test
    void apiErrorDto_whenDeserialized_thenTimestampParsedAndFieldsMatch() throws JsonProcessingException {
        String json = "{\"timestamp\":\"2025-08-27T12:00:00\",\"status\":400,\"error\":\"Bad Request\",\"code\":\"400\",\"message\":\"Validation Failed\",\"path\":\"/api/users\",\"fieldErrors\":[{\"field\":\"test\",\"rejectedValue\":\"@#\",\"message\":\"Test Message\"}]}";

        ObjectMapper objectMapper1 = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ApiErrorResponseDTO dto = objectMapper1.readValue(json, ApiErrorResponseDTO.class);

        assertEquals(LocalDateTime.of(2025,8,27,12,0), dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("400", dto.getCode());
        assertEquals("Validation Failed", dto.getMessage());
        assertEquals("/api/users", dto.getPath());
        assertEquals("test", dto.getFieldErrors().getFirst().getField());
        assertEquals("@#", dto.getFieldErrors().get(0).getRejectedValue());
        assertEquals("Test Message", dto.getFieldErrors().getFirst().getMessage());
    }




}