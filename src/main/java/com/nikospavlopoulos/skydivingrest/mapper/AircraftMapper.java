package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.AircraftLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AircraftMapper {

    // Maps Aircraft Entity to AircraftLookupDTO

    AircraftLookupDTO aircraftToLookupDTO(Aircraft aircraft);

}
