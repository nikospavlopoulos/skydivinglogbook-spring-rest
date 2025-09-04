package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.exceptions.UnauthorizedException;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationRequestDTO;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationResponseDTO;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetails;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetailsService;
import com.nikospavlopoulos.skydivingrest.security.jwt.JwtServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;
    private final UserRepository userRepository;

    @Override
    public AuthenticationResponseDTO authenticateUser(AuthenticationRequestDTO requestDTO) throws UnauthorizedException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority) // get "ROLE_SKYDIVER"
                .map(r -> r.replace("ROLE_", ""))
                .findFirst()
                .orElseThrow()
                ;

        // Create token if successful authentication

        String token = jwtService.generateToken(userDetails.getUsername(), role);

        // Return DTO
        return new AuthenticationResponseDTO(token, userDetails.getUsername(), role);
    }
}
