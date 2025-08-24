package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface JumpMapper {

    // Maps JumpInsertDTO to Jump Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "aircraft", ignore = true)
    @Mapping(target = "dropzone", ignore = true)
    @Mapping(target = "jumptype", ignore = true)
    @Mapping(target = "user", ignore = true)
    Jump jumpInsertDTOtoJumpEntity(JumpInsertDTO jumpInsertDTO);

    // TODO: Handle Aircraft, Dropzone, JumpType, User at the service layer before saving


    // Maps Jump Entity to JumpLookupDTO
    JumpLookupDTO jumpToJumpLookupDTO(Jump jump);
}
