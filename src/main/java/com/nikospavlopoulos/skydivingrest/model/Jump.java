package com.nikospavlopoulos.skydivingrest.model;

import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
@Table(name = "jump")
public class Jump extends AbstractEntity {

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

    @Positive
    @Column(nullable = false)
    private Integer altitude;

    @Positive
    @Column(nullable = false)
    private Integer freeFallDuration;

    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime jumpDate;

    private String jumpNotes;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
