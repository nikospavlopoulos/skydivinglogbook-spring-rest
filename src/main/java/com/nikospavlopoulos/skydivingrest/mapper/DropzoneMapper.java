package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.DropzoneLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DropzoneMapper {

    // Maps Dropzone Entity to DropzoneLookupDTO

    DropzoneLookupDTO dropzoneToDropzoneLookupDTO(Dropzone dropzone);
}
