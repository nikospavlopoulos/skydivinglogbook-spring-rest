package com.nikospavlopoulos.skydivingrest.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

// GENERATE TOKEN
    /// Happy path - Token Contains Subject, Roles, Timestamps and isSigned
    @Test
    void generateToken_whenGenerated_ContainsAllClaims() {

        // TODO: ADJUST THE CLOCK BEFORE RUNNING TEST
        Instant fixedInstant = Instant.parse("2025-09-03T20:00:00Z");

        byte[] keyBytes = Base64.getDecoder().decode("SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        JwtServiceImpl jwtService = factorySetUp(clock);

//        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60);

        String token = jwtService.generateToken("testUsername", "Skydiver");

        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        // Assert
        assertEquals("testUsername", claims.getPayload().getSubject());

        Instant expectedExpiration = fixedInstant.plus(60, ChronoUnit.MINUTES);
        Instant tokenExpiration = claims.getPayload().getExpiration().toInstant();

        assertEquals(expectedExpiration, tokenExpiration);
        assertEquals("SKYDIVER", claims.getPayload().get("role", String.class));

    }


// VALIDATE TOKEN
    /// returns true for a fresh token
    @Test
    void validateToken_whenFreshToken_ReturnsTrue() {

        // TODO: ADJUST THE CLOCK BEFORE RUNNING TEST
        Instant fixedInstant = Instant.parse("2025-09-03T20:00:00Z");

        byte[] keyBytes = Base64.getDecoder().decode("SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        JwtServiceImpl jwtService = factorySetUp(clock);

        String token = jwtService.generateToken("testUsername", "Skydiver");

        // Assert

        assertTrue(jwtService.validateToken(token));
    }


    /// returns false for an expired token
    @Test
    void validateToken_whenExpiredToken_ReturnsFalse() {
        // TODO: ADJUST THE CLOCK BEFORE RUNNING TEST
        Instant fixedInstant = Instant.parse("2025-09-03T18:30:00Z");

        byte[] keyBytes = Base64.getDecoder().decode("SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        JwtServiceImpl jwtService = factorySetUp(clock);

        String token = jwtService.generateToken("testUsername", "Skydiver");

        // Assert

        assertFalse(jwtService.validateToken(token));
    }


    /// returns false for wrong signature
    @Test
    void validateToken_whenWrongSignature_ReturnsFalse() {

        // TODO: ADJUST THE CLOCK BEFORE RUNNING TEST
        Instant fixedInstant = Instant.parse("2025-09-03T20:00:00Z");

//        byte[] keyBytes = Base64.getDecoder().decode("SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        byte[] keyBytes = Base64.getDecoder().decode("WrongSignaturewWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        SecretKey wrongKey = Keys.hmacShaKeyFor(keyBytes);

        Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        JwtServiceImpl jwtService = factorySetUp(clock);

        String wrongToken = Jwts.builder()
                .subject("testUsername")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(5,ChronoUnit.MINUTES)))
                 .signWith(wrongKey)
                  .compact();

        // Assert
        assertFalse(jwtService.validateToken(wrongToken));
    }

    /// Validation Test idea - returns false for malformed input


// EXTRACT USERNAME
    /// returns subject for a valid token
    @Test
    void extractUsername_whenValidToken_returnsSubject() {
        // TODO: ADJUST THE CLOCK BEFORE RUNNING TEST
        Instant fixedInstant = Instant.parse("2025-09-03T20:00:00Z");

        byte[] keyBytes = Base64.getDecoder().decode("SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        JwtServiceImpl jwtService = factorySetUp(clock);

        String token = jwtService.generateToken("testUsername", "Skydiver");

        // Assert
        assertEquals("testUsername", jwtService.extractUsername(token));

    }


    // Helper

    JwtServiceImpl factorySetUp(Clock clock) {
        JwtServiceImpl jwtService = new JwtServiceImpl(clock);

        ReflectionTestUtils.setField(jwtService, "secretKey", "SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60);

        return jwtService;
    }

}