package com.nikospavlopoulos.skydivingrest.security;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import com.nikospavlopoulos.skydivingrest.security.jwt.JwtServiceImpl;
import com.nikospavlopoulos.skydivingrest.security.jwt.JwtTestUtilsHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;


import java.time.Clock;
import java.util.stream.Stream;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private JwtServiceImpl jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Clock clock;
    @Autowired
    private MockMvc mockMvc;

    private static User user;
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static JwtServiceImpl staticJwtService;



    // Happy Path - Valid Token - Returns 200 OK
    @Test
    void accessProtected_withValidToken_returns200() throws Exception {

        String token = jwtService.generateToken(user.getUsername(), user.getRole().toString());

        mockMvc.perform(get("/api/jump/all")
                .header(AUTH_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

    }

    // Test negative scenarios
    ///  Missing token, Malformed Header, Invalid Signature, Expired Token

    @Test
    void accessProtected_whenInvalidTokens_returns401() throws Exception {
        String validEndpoint = "/api/jump/all";

        // Missing token
        mockMvc.perform(get(validEndpoint))
                .andExpect(status().isUnauthorized());

        // Malformed header
        mockMvc.perform(get(validEndpoint).header(AUTH_HEADER, "WrongAuthorization"))
                .andExpect(status().isUnauthorized());

        // Invalid signature
        String invalidToken = JwtTestUtilsHelper.generateInvalidSignatureToken(
                user.getUsername(), user.getRole().toString());
        mockMvc.perform(get(validEndpoint)
                        .header(AUTH_HEADER, BEARER_PREFIX + invalidToken))
                .andExpect(status().isUnauthorized());

        // Expired token
        String expiredToken = JwtTestUtilsHelper.generateExpiredToken(
                jwtService, user.getUsername(), user.getRole().toString());
        mockMvc.perform(get(validEndpoint)
                        .header(AUTH_HEADER, BEARER_PREFIX + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    //TODO: Scenario - Wrong Role (insufficient privileges returns) - Returns 403 Forbidden

    @BeforeEach
    public void createValidUser(){
        user = new User();
        user.setUsername("username@test.com");
        user.setPassword("a@123456");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");
        user.setRole(Role.SKYDIVER);
        userRepository.save(user);
    }

    @BeforeEach
    public void jwtFactorySetUp() {

        ReflectionTestUtils.setField(jwtService, "secretKey", "SyKNDTIxUc5Pa0wWU8Kg9xsWIh5OLNMv58iCQeLwTBM=");

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60);
    }

}

/*
    @Test
    void accessProtected_whenInvalidTokens_returns401() throws Exception {
        String endpoint = "/api/jump/all";

        // Missing token
        mockMvc.perform(get(endpoint))
                .andExpect(status().isUnauthorized());

        // Malformed header
        mockMvc.perform(get(endpoint)
                        .header(AUTH_HEADER, "WrongAuthorization"))
                .andExpect(status().isUnauthorized());

        // Invalid signature
        String invalidToken = JwtTestUtilsHelper.generateInvalidSignatureToken(
                user.getUsername(), user.getRole().toString());
        mockMvc.perform(get(endpoint)
                        .header(AUTH_HEADER, BEARER_PREFIX + invalidToken))
                .andExpect(status().isUnauthorized());

        // Expired token
        String expiredToken = JwtTestUtilsHelper.generateExpiredToken(
                jwtService, user.getUsername(), user.getRole().toString());
        mockMvc.perform(get(endpoint)
                        .header(AUTH_HEADER, BEARER_PREFIX + expiredToken))
                .andExpect(status().isUnauthorized());
    }
*/





