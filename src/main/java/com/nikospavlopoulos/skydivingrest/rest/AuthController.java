package com.nikospavlopoulos.skydivingrest.rest;

import com.nikospavlopoulos.skydivingrest.core.exceptions.UnauthorizedException;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationRequestDTO;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationResponseDTO;
import com.nikospavlopoulos.skydivingrest.service.AuthenticationServiceImpl;
import com.nikospavlopoulos.skydivingrest.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller handling authentication requests.
 * Delegates user login to the AuthenticationService and returns a JWT response.
 */


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody @Valid AuthenticationRequestDTO requestDTO) throws UnauthorizedException {
        AuthenticationResponseDTO responseDTO = authenticationService.authenticateUser(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

}