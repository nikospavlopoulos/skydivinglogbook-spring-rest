package com.nikospavlopoulos.skydivingrest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class PasswordUpdateDTOTest {

    private Validator validator;

    // Initialize Validator Before Each Test
    @BeforeEach
    public void initializeValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Test passwordUpdateDto -> ValidValues -> PassesValidation
    @Test
    void passwordUpdateDto_ValidValues_PassesValidation() {
        PasswordUpdateDTO password = new PasswordUpdateDTO(
                "a@123456",
                "a@456789"
        );

        Set<ConstraintViolation<PasswordUpdateDTO>> violations = validator.validate(password);

        // Asserts

        assertEquals(0, violations.size());
    }


    // Test passwordUpdateDto -> InvalidValuesBlank -> FailsValidation
    @Test
    void passwordUpdateDto_InvalidValuesBlank_FailsValidation() {
        PasswordUpdateDTO password = new PasswordUpdateDTO(
                "",
                "a@456789"
        );

        Set<ConstraintViolation<PasswordUpdateDTO>> violations = validator.validate(password);

        // Asserts

        assertEquals(1, violations.size());
    }



    // Test passwordUpdateDto -> InvalidValues -> FailsValidation
    @Test
    void passwordUpdateDto_InvalidValues_FailsValidation() {
        PasswordUpdateDTO password = new PasswordUpdateDTO(
                "a@123456",
                "@46789"
        );

        Set<ConstraintViolation<PasswordUpdateDTO>> violations = validator.validate(password);

        // Asserts

        assertEquals(1, violations.size());
    }

}