package com.nikospavlopoulos.skydivingrest.dto.lookup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Data Transfer Object (DTO) for representing a jump record in a read-only manner.
 * This class is used to transfer data about a jump without allowing modifications.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JumpLookupDTO {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long id;
    private UUID uuid;

    private Integer altitude;
    private Integer freeFallDuration;
    private LocalDateTime jumpDate;
    private String jumpNotes;

    private AircraftLookupDTO aircraft;
    private DropzoneLookupDTO dropzone;
    private JumptypeLookupDTO jumptype;

    private UserLookupDTO user;
}
