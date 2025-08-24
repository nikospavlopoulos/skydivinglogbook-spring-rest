package com.nikospavlopoulos.skydivingrest.dto;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for user registration input
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInsertDTO {

    @NotEmpty(message = "Username (your email) is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    @NotEmpty (message = "Password is required")
    @Pattern(regexp = "^(?=(.*[A-Za-z]){1})(?=(.*\\d){1})(?=(.*[!@#$%^&*(),.?\":{}|<>]){1}).{8,}$", message = "Password must be at least 8 characters long, include letters, numbers, and one special character")
    private String password; // TODO: Password Encryption


    private String firstname; //Optional

    private String lastname; //Optional

}
