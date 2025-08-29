package com.nikospavlopoulos.skydivingrest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for updating existing user
 * Updates only details, not passwords
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {

    @Email(message = "Username must be a valid email address")
    private String username;

    private String firstname;

    private String lastname;

}
