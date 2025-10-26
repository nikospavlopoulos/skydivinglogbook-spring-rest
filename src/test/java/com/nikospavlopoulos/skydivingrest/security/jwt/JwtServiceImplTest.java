package com.nikospavlopoulos.skydivingrest.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String BASE64_TEST_SECRET = "SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=";
    private static final String BASE64_WRONG_TEST_SECRET = "WrongTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=";
    private static final long TEST_EXPIRATION = 60L;
    Instant fixedInstant = Instant.parse("2025-09-06T21:00:00Z");

    private SecretKey secretKey;
    private JwtServiceImpl jwtService;

    @BeforeEach
    void beforeEach() {
        // Set Up service - DRY
        byte[] keyBytes = Base64.getDecoder().decode(BASE64_TEST_SECRET);
        secretKey = Keys.hmacShaKeyFor(keyBytes);

        Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        jwtService = factorySetUp(clock);
    }


// GENERATE TOKEN
    /// Happy path - Token Contains Subject, Roles, Timestamps and isSigned
    @Test
    void generateToken_whenGenerated_ContainsAllClaims() {

        String token = jwtService.generateToken("testUsername", "Skydiver");

        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .clock(() -> Date.from(fixedInstant))
                .build()
                .parseSignedClaims(token);

        // Assert
        assertEquals("testUsername", claims.getPayload().getSubject());

        Instant expectedExpiration = fixedInstant.plus(TEST_EXPIRATION, ChronoUnit.MINUTES);
        Instant tokenExpiration = claims.getPayload().getExpiration().toInstant();

        assertEquals(expectedExpiration, tokenExpiration);

        assertEquals("Skydiver", claims.getPayload().get("role", String.class));

    }


// VALIDATE TOKEN
    /// returns true for a fresh token
    @Test
    void validateToken_whenFreshToken_ReturnsTrue() {

        String token = jwtService.generateToken("testUsername", "Skydiver");

        // Assert

        assertTrue(jwtService.validateToken(token));
    }


    /// returns false for an expired token
    @Test
    void validateToken_whenExpiredToken_ReturnsFalse() {

        // Creating a past token (fixed - 90minutes)
        Clock clockCreation = Clock.fixed(fixedInstant.minus(TEST_EXPIRATION + 1,ChronoUnit.MINUTES), ZoneId.of("UTC"));

        JwtServiceImpl jwtCreation = factorySetUp(clockCreation);

        String token = jwtCreation.generateToken("testUsername", "Skydiver");

        // Validating token with present(fixed) time

        Clock clockValidation = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        JwtServiceImpl jwtValidation = factorySetUp(clockValidation);

        // Assert

        assertFalse(jwtValidation.validateToken(token));
    }


    /// returns false for wrong signature
    @Test
    void validateToken_whenWrongSignature_ReturnsFalse() {

        byte[] keyBytes = Base64.getDecoder().decode(BASE64_WRONG_TEST_SECRET);

        SecretKey wrongKey = Keys.hmacShaKeyFor(keyBytes);

        String wrongToken = Jwts.builder()
                .subject("testUsername")
                .issuedAt(Date.from(fixedInstant))
                .expiration(Date.from(fixedInstant.plus(TEST_EXPIRATION,ChronoUnit.MINUTES)))
                 .signWith(wrongKey)
                  .compact();

        // Assert
        assertFalse(jwtService.validateToken(wrongToken));
    }

// EXTRACT USERNAME
    /// returns subject for a valid token
    @Test
    void extractUsername_whenValidToken_returnsSubject() {

        String token = jwtService.generateToken("testUsername", "Skydiver");

        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .clock(() -> Date.from(fixedInstant))
                .build()
                .parseSignedClaims(token);

        // Assert
        assertEquals("testUsername", claims.getPayload().getSubject());

    }


    // Helper

    JwtServiceImpl factorySetUp(Clock clock) {
        JwtServiceImpl jwtService = new JwtServiceImpl(clock);

        ReflectionTestUtils.setField(jwtService, "secretKey", BASE64_TEST_SECRET);

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);

        return jwtService;
    }

}