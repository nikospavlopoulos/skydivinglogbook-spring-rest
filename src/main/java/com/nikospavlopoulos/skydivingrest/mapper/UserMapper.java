package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.User;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface UserMapper {

    // Mapping user to UserLookupDTO
    UserLookupDTO userToUserLookupDTO(User user);
}
