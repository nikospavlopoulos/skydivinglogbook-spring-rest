package com.nikospavlopoulos.skydivingrest.model.static_data;

import com.nikospavlopoulos.skydivingrest.model.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Jumptype Entity in the Application
 * Entity maps to the 'jumptype' table in the database
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jumptype")
public class Jumptype extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String jumptypeName;

}
