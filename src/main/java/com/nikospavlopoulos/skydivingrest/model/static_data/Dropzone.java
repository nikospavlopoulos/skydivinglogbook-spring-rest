package com.nikospavlopoulos.skydivingrest.model.static_data;

import com.nikospavlopoulos.skydivingrest.model.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dropzone Entity in the Application
 * Entity maps to the 'dropzone' table in the database
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dropzone")
public class Dropzone extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String dropzoneName;

}
