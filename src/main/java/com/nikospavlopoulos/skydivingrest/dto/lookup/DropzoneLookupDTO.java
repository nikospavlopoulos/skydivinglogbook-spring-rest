package com.nikospavlopoulos.skydivingrest.dto.lookup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) for representing a Dropzone record in a read-only manner.
 * This class is used to transfer data about a Dropzone without allowing modifications.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DropzoneLookupDTO {

    private Long id;

    private String dropzoneName;

}
