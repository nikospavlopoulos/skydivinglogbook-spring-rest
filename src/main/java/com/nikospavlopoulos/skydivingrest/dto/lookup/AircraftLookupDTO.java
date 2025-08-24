package com.nikospavlopoulos.skydivingrest.dto.lookup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) for representing an Aircraft record in a read-only manner.
 * This class is used to transfer data about an Aircraft without allowing modifications.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftLookupDTO {

    private Long id;

    private String aircraftName;

}
