package com.nikospavlopoulos.skydivingrest.dto.lookup;
/**
 * Data Transfer Object (DTO) for representing a user record in a read-only manner.
 * This class is used to transfer data about a user without allowing modifications.
 */

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) for representing a jumptype record in a read-only manner.
 * This class is used to transfer data about a jumptype without allowing modifications.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLookupDTO {

    private Long id;
    private UUID uuid;

    private String username;

    private String firstname;
    private String lastname;

    private Role role;

    private Boolean active;

}
