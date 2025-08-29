package com.nikospavlopoulos.skydivingrest.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateDTOTest {

    private Validator validator;

    // Initialize Validator Before Each Test
    @BeforeEach
    public void initializeValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Test ValidValues -> PassValidation

    @Test
    void userUpdateDto_ValidValues_PassesValidation() {
        UserUpdateDTO user = new UserUpdateDTO(
                "username@test.com",
                "firstname",
                "lastname"
        );


        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(user);

                // Asserts

        assertEquals(0, violations.size());
    }


    // Test ValidValues -> EmptyFields -> PassValidation

    @Test
    void userUpdateDto_ValidValuesEmpty_PassesValidation() {
        UserUpdateDTO user = new UserUpdateDTO(
                "username@test.com",
                "",
                ""
        );


        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(user);

        // Asserts

        assertEquals(0, violations.size());
    }

    // Test InvalidValues -> FailValidation

    @Test
    void userUpdateDto_InvalidValues_FailsValidation() {
        UserUpdateDTO user = new UserUpdateDTO(
                "username",
                "firstname",
                "lastname"
        );


        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(user);

        // Asserts

        assertEquals(1, violations.size());

        ConstraintViolation<UserUpdateDTO> violation = violations.iterator().next();

        assertEquals("username", violation.getPropertyPath().toString());

        assertTrue(violation.getMessage().contains("valid email"));
    }


    // Test InvalidValues -> EmptyUsername -> FailsValidation

    @Test
    void userUpdateDto_InvalidValuesEmpty_PassesValidation() {
        UserUpdateDTO user = new UserUpdateDTO(
                "",
                "",
                ""
        );


        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(user);

        // Asserts

        assertEquals(0, violations.size());
    }

    // Test InvalidValues -> BlankUsername -> FailsValidation

    @Test
    void userUpdateDto_InvalidValuesBlank_FailsValidation() {
        UserUpdateDTO user = new UserUpdateDTO(
                "  ",
                "",
                ""
        );


        Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(user);

        // Asserts

        assertEquals(1, violations.size());
    }



}