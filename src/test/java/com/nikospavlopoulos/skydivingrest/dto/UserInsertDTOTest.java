package com.nikospavlopoulos.skydivingrest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserInsertDTOTest {

    @Autowired
    private Validator validator;

    // Happy path: Verify that there are no violations
    @Test
    void userInsertDTOSuccessTest() {
        UserInsertDTO dto = validUserInsertDTO_passes();

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(0, violations.size());

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Test Invalid Email
    @Test
    void userInsertDTOInvalidEmailTest() {
        UserInsertDTO dto = invalidEmail_fails();

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(
                v -> v.getPropertyPath().toString().equals("username")
        ));

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Test Invalid Password
    @Test
    void userInsertDTOInvalidPasswordTest() {
        UserInsertDTO dto = invalidPassword_fails();

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(
                v -> v.getPropertyPath().toString().equals("password")
        ));

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Test Null Username
    @Test
    void userInsertDTONullUsernameTest() {
        UserInsertDTO dto = validUserInsertDTO_passes();
        dto.setUsername(null);

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(
                v -> v.getPropertyPath().toString().equals("username")
        ));

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Test Empty Username
    @Test
    void userInsertDTOEmptyUsernameTest() {
        UserInsertDTO dto = validUserInsertDTO_passes();
        dto.setUsername("");

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(
                v -> v.getPropertyPath().toString().equals("username")
        ));

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Test Null Password
    @Test
    void userInsertDTONullPasswordTest() {
        UserInsertDTO dto = validUserInsertDTO_passes();
        dto.setPassword(null);

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(
                v -> v.getPropertyPath().toString().equals("password")
        ));

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Test Empty Password
    @Test
    void userInsertDTOEmptyPasswordTest() {
        UserInsertDTO dto = validUserInsertDTO_passes();
        dto.setPassword("");

        Set<ConstraintViolation<UserInsertDTO>> violations = validator.validate(dto);

        // Asserts
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(
                v -> v.getPropertyPath().toString().equals("password")
        ));

        // Prints
        for (ConstraintViolation<UserInsertDTO> violation : violations) {
            System.out.println("---");
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Error: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    // Helper Functions

    private UserInsertDTO validUserInsertDTO_passes() {
        return new UserInsertDTO(
                "test@test.com",
                "a@123456",
                "Firstname",
                "Lastname");
    }

    private UserInsertDTO invalidEmail_fails() {
        return new UserInsertDTO(
                "test",
                "a@123456",
                "Firstname",
                "Lastname");
    }

    private UserInsertDTO invalidPassword_fails() {
        return new UserInsertDTO(
                "test@test.com",
                "3456",
                "Firstname",
                "Lastname");
    }

}