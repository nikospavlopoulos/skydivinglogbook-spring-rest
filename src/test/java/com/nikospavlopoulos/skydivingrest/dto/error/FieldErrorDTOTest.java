package com.nikospavlopoulos.skydivingrest.dto.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldErrorDTOTest {

    // Test ensure the class stores data
    @Test
    void fieldErrorDtoSuccess() {
        FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(
                "test",
                "@#",
                "TestMessage"
        );

        assertEquals("test", fieldErrorDTO.getField());
        assertEquals("@#", fieldErrorDTO.getRejectedValue());
        assertEquals("TestMessage", fieldErrorDTO.getMessage());

    }

    // Test - DTO correctly serializes to JSON
    @Test
    void fieldErrorDtoToJSON() throws JsonProcessingException {
        FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(
                "test",
                "@#",
                "TestMessage"
        );

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(fieldErrorDTO);

        Map<String,Object> map = objectMapper.readValue(json, Map.class);

        assertEquals("test", map.get("field"));
        assertEquals("@#", map.get("rejectedValue"));
        assertEquals("TestMessage", map.get("message"));
    }


    // Test - JSON correctly deserializes to DTO
    @Test
    void jsonToFieldErrorDto() throws JsonProcessingException {
        String json =
                "{ \"field\":\"test\",                \"rejectedValue\":\"@#\",\"message\":\"TestMessage\"}";

        ObjectMapper objectMapper = new ObjectMapper();

        FieldErrorDTO fieldErrorDTO = objectMapper.readValue(json, FieldErrorDTO.class);

        assertEquals("test", fieldErrorDTO.getField());
        assertEquals("@#", fieldErrorDTO.getRejectedValue());
        assertEquals("TestMessage", fieldErrorDTO.getMessage());
    }

    // Test Rejected Value Serializes as Null
    @Test
    void jsonRejectedValueNull() throws JsonProcessingException {
         FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(
                    "test",
                    null,
                    "TestMessage"
            );

            ObjectMapper objectMapper = new ObjectMapper();

            String json = objectMapper.writeValueAsString(fieldErrorDTO);

            Map<String,Object> map = objectMapper.readValue(json, Map.class);

            assertEquals("test", map.get("field"));
            assertNull(map.get("rejectedValue"));
            assertEquals("TestMessage", map.get("message"));
    }

}