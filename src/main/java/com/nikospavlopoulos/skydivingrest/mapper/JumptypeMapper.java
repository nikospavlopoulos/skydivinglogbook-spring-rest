package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.JumptypeLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JumptypeMapper {

    // Maps Jumptype entity to JumptypeLookupDTO
    JumptypeLookupDTO jumptypeToJumptypeLookupDTO(Jumptype jumptype);
}
