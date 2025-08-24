package com.nikospavlopoulos.skydivingrest.dto.lookup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) for representing a jumptype record in a read-only manner.
 * This class is used to transfer data about a jumptype without allowing modifications.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JumptypeLookupDTO {

    private Long id;

    private String jumptypeName;

}
