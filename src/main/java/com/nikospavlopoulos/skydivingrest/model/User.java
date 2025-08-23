package com.nikospavlopoulos.skydivingrest.model;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * User entity in the database
 * Implements {@link UserDetails//} interface required from Spring Security // TODO: Activate Security - Implement UserDetails
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    // Automatically Create UUID
    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    @Column(nullable = false, unique = true)
    @Email
    private String username;

    @Column(nullable = false)
    private String password; // TODO: Password Encryption

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

}
