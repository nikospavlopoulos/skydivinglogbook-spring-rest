package com.nikospavlopoulos.skydivingrest.security.jwt;

import com.nikospavlopoulos.skydivingrest.core.exceptions.UnauthorizedException;
import com.nikospavlopoulos.skydivingrest.model.User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service for generating, validating, and parsing JWT tokens.
 * Responsibilities:
 * - Create signed JWTs containing username and roles.
 * - Validate tokens (check signature + expiration).
 * - Extract claims such as username from valid tokens.
 */

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements IJwtService{

    // Secret signing key (Base64 encoded) injected from application.properties
    @Value("${security.jwt.secret}")
    private String secretKey;

    // Token expiration duration in minutes (also from configuration)
    @Value("${security.jwt.expiration-minutes}")
    private long jwtExpiration;

    // Time source (makes testing easier and avoids relying on system default)
    private final Clock clock;

    /**
     * Generates a JWT token with subject (username) and role.
     *
     * @param username the authenticated user's name
     * @param role    list of role for authorization
     * @return a signed JWT as a String
     */
    @Override
    public String generateToken(String username, String role) {
        // Calculate timestamps based on injected clock
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(jwtExpiration, ChronoUnit.MINUTES);
        // Add custom claims (e.g., role)
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        // Build and sign the JWT
        return Jwts.builder()
//                .issuer("deployment_url")
                .claims(claims)     // custom claims
                .subject(username)  // standard claim: subject = username
                .issuedAt(Date.from(issuedAt))  // token issue time
                .expiration(Date.from(expiresAt))   // token expiration time
                .signWith(getSigninKey())   // sign with secret key
                .compact();
    }

    /**
     * Validates a JWT token by checking signature and expiration.
     * @param token the JWT string
     * @return true if the token is valid, false otherwise
     */
    @Override
    public boolean validateToken(String token) {

        JwtParser jwtParser = Jwts.parser()
                .verifyWith(getSigninKey()) // enforce correct signature
                .clock(() -> Date.from(clock.instant()))
                .build();
        try {

        return jwtParser.parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .after(Date.from(clock.instant()));
        } catch (Exception e) {
            return false; // Any other Failure (Signature, Malformed etc)
        }

    }

    /**
     * Extracts the username (subject) from a JWT.
     *
     * @param token the JWT string
     * @return the subject (username) embedded in the token
     */
    @Override
    public String extractUsername(String token) {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(getSigninKey())
                .clock(() -> Date.from(clock.instant()))
                .build();

        return jwtParser.parseSignedClaims(token).getPayload().getSubject();
    }

    // Helper Converts Base64 secret string into a SecretKey object.
    private SecretKey getSigninKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
