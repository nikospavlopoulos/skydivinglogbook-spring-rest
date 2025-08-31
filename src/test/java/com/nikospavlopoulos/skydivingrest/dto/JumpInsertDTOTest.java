package com.nikospavlopoulos.skydivingrest.dto;

import com.nikospavlopoulos.skydivingrest.model.Jump;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.validation.Validator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JumpInsertDTOTest {

    @Autowired
    private Validator validator;

    // Test no constraint violations
    @Test
    void jumpInsertDTOSucessTest() {
        JumpInsertDTO dto = new JumpInsertDTO(
                10000,
                60,
                LocalDateTime.now(),
                "Notes",
                1L,
                1L,
                1L,
                1L);

        Set<ConstraintViolation<JumpInsertDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Violations NOT expected");
    }

    // Tests altitude constraints
    @ParameterizedTest
    @ValueSource(ints = {2500, 3000, 10000, 16000, 21000})
    void altitudeConstraintTest(int altitude){
        JumpInsertDTO dto = new JumpInsertDTO(
                altitude,
                60,
                LocalDateTime.now(),
                "Notes",
                1L,
                1L,
                1L,
                1L);

        Set<ConstraintViolation<JumpInsertDTO>> violations = validator.validate(dto);

        switch (altitude) {
            case 2500, 21000: // off limits
                assertFalse(violations.isEmpty(), "Expected Violation for Altitude: " + altitude);
                break;
            case 3000, 10000, 16000:
                assertTrue(violations.isEmpty(), "NOT Expected Violation for Altitude: " + altitude);
                break;
        }
    }

    // Tests freeFall duration constraints
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 30, 70, 101})
    void freeFallDurationTest(int freeFallDuration){
        JumpInsertDTO dto = new JumpInsertDTO(
                10000,
                freeFallDuration,
                LocalDateTime.now(),
                "Notes",
                1L,
                1L,
                1L,
                1L
        );

        Set<ConstraintViolation<JumpInsertDTO>> violations = validator.validate(dto);

        if (freeFallDuration < 0 || freeFallDuration > 100) {
            assertFalse(violations.isEmpty(),"Expected Violation for freeFallDuration: " + freeFallDuration);
        } else {
            assertTrue(violations.isEmpty(), "NOT Expected Violations for freeFallDuration: " + freeFallDuration);
        }
    }

    // Test date constraints (not Future date)
    @ParameterizedTest
    @MethodSource("dateProvider")
    void jumpDateTest(LocalDateTime jumpDate, boolean isValid){
        JumpInsertDTO dto = new JumpInsertDTO(
                10000,
                50,
                jumpDate,
                "Notes",
                1L,
                1L,
                1L,
                1L
        );

        Set<ConstraintViolation<JumpInsertDTO>> violations = validator.validate(dto);

        if (isValid) {
            assertTrue(violations.isEmpty(), "NOT Expected Violation for date: " + jumpDate);
        } else {
            assertFalse(violations.isEmpty(), "Expected Violation for date: " + jumpDate);
        }

    }

    // Helper function to provide past, present, future dates
    static Stream<Arguments> dateProvider() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().minusDays(1), true),
                Arguments.of(LocalDateTime.now(), true),
                Arguments.of(LocalDateTime.now().plusDays(1), false)
                );
    }


    // Tests max length constraint on jumpNotes
    @ParameterizedTest
    @MethodSource("jumpNotesProvider")
    void jumpNotesTest(String notes, boolean isValid){
        JumpInsertDTO dto = new JumpInsertDTO(
                10000,
                50,
                LocalDateTime.now(),
                notes,
                1L,
                1L,
                1L,
                1L
        );

        Set<ConstraintViolation<JumpInsertDTO>> violations = validator.validate(dto);

        if (isValid) {
            assertTrue(violations.isEmpty(), "NOT expected Violation for notes: " + notes);
        } else {
            assertFalse(violations.isEmpty(), "Expected violation for notes: " + notes);
        }

    }

    // Helper function to create string notes of various lenghts
    static Stream<Arguments> jumpNotesProvider() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of("Notes", true),
                Arguments.of("c".repeat(500), true),
                Arguments.of("c".repeat(501), false)
        );
    }


    // Tests @NotNull fields
    @ParameterizedTest
    @MethodSource("nullProvider")
    void JumpInsertDTO_notNullTests(String field, Object value){
        JumpInsertDTO dto = new JumpInsertDTO(
                10000,
                60,
                LocalDateTime.now(),
                "Notes",
                1L,
                1L,
                1L,
                1L);

        switch(field) {
            case "altitude":
                dto.setAltitude((Integer) value);
                break;
            case "freeFallDuration":
                dto.setFreeFallDuration((Integer) value);
                break;
            case "jumpDate":
                dto.setJumpDate((LocalDateTime) value);
                break;
            case "aircraftId":
                dto.setAircraftId((Long) value);
                break;
            case "dropzoneId":
                dto.setDropzoneId((Long) value);
                break;
            case "jumptypeId":
                dto.setJumptypeId((Long) value);
                break;
            case "userId":
                dto.setUserId((Long) value);
                break;
        }

        Set<ConstraintViolation<JumpInsertDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Expected Violation for null value in: " + field);
    }

// Helper function to pair each field with null to test @NotNull
static Stream<Arguments> nullProvider() {
        return Stream.of(
                Arguments.of("altitude", null),
                Arguments.of("freeFallDuration", null),
                Arguments.of("jumpDate", null),
                Arguments.of("aircraftId", null),
                Arguments.of("dropzoneId", null),
                Arguments.of("jumptypeId", null),
                Arguments.of("userId", null)
                );
    }

}