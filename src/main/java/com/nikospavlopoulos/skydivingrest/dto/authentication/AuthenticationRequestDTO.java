package com.nikospavlopoulos.skydivingrest.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDTO {

    @NotBlank(message = "Username (your email) is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    @NotBlank (message = "Password is required")
    @Pattern(regexp = "^(?=(.*[A-Za-z]){1})(?=(.*\\d){1})(?=(.*[!@#$%^&*(),.?\":{}|<>]){1}).{8,}$", message = "Password must be at least 8 characters long, include letters, numbers, and one special character")
    private String password;

}
