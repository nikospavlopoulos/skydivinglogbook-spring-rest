package com.nikospavlopoulos.skydivingrest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for updating existing user's password
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordUpdateDTO {

    @NotBlank
    private String oldPassword;

    @Pattern(regexp = "^(?=(.*[A-Za-z]){1})(?=(.*\\d){1})(?=(.*[!@#$%^&*(),.?\":{}|<>]){1}).{8,}$", message = "Password must be at least 8 characters long, include letters, numbers, and one special character")
    private String newPassword;

}
