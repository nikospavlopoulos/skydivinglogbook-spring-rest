package com.nikospavlopoulos.skydivingrest.security.jwt;

import java.util.List;

public interface IJwtService {

    String generateToken(String username, List<String> roles);

    boolean validateToken(String token);

    String extractUsername(String token);

}
