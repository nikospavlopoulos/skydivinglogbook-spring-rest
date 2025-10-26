package com.nikospavlopoulos.skydivingrest.security.jwt;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
public class JwtTestUtilsHelper {

    private static JwtServiceImpl jwtService = new JwtServiceImpl(Clock.systemUTC());

    public static String generateValidToken(String username, String role) {
        return jwtService.generateToken(username, role);
    }

    public static String generateExpiredToken(JwtServiceImpl jwtService, String username, String role) {

        // Overide the clock
        Clock pastClock = Clock.fixed(Instant.now().minus(120,ChronoUnit.MINUTES), ZoneOffset.UTC);
        ReflectionTestUtils.setField(jwtService, "clock", pastClock);

        // Generate the token
        String token = jwtService.generateToken(username, role);

        // Reset clock
        ReflectionTestUtils.setField(jwtService, "clock", Clock.systemUTC());

        return token;
    }

    public static String generateInvalidSignatureToken(String username, String role) {

        JwtServiceImpl invalidService = new JwtServiceImpl(Clock.systemUTC());
        ReflectionTestUtils.setField(invalidService, "secretKey", "WrongSecretKeywWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60);

        return invalidService.generateToken(username, role);
         }



}
