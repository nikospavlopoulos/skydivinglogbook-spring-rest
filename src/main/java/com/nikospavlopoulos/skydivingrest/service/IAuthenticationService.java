package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationRequestDTO;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationResponseDTO;

public interface IAuthenticationService {

    AuthenticationResponseDTO authenticateUser(AuthenticationRequestDTO dto);

}
