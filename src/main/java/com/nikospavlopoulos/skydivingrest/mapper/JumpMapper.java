package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.JumpUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

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

    // Maps Jump Entity to JumpLookupDTO
    JumpLookupDTO jumpToJumpLookupDTO(Jump jump);


    // Maps Jump Update DTO to Jump - MapStruct to update Entity in place
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    void jumpUpdateDtoToJumpEntity(JumpUpdateDTO jumpUpdateDTO, @MappingTarget Jump jumpEntity);

    // Mapping jump lists to JumpLookupDTO
    default Page<JumpLookupDTO> jumpListToJumpLookupDTO(Page<Jump> jumps) {
        return jumps.map(this::jumpToJumpLookupDTO);
    };

}
