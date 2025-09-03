package com.nikospavlopoulos.skydivingrest.model;

import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.time.LocalDateTime;


import java.util.UUID;

/**
 * Skydive Jump Entity in the Database
 * Maps to the "jump" table in DB
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jump", indexes = {@Index(name = "idx_user_date_jump", columnList = "user_id, jumpDate, jumptype_id, id")})
public class Jump extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    // Automatically Create UUID
    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    @Positive
    @Column(nullable = false)
    private Integer altitude;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer freeFallDuration;

    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime jumpDate;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @ManyToOne
    @JoinColumn(name = "dropzone_id", nullable = false)
    private Dropzone dropzone;

    @ManyToOne
    @JoinColumn(name = "jumptype_id", nullable = false)
    private Jumptype jumptype;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    private String jumpNotes;

}
