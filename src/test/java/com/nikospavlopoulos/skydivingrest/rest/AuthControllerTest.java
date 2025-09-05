package com.nikospavlopoulos.skydivingrest.rest;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.core.exceptions.UnauthorizedException;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationRequestDTO;
import com.nikospavlopoulos.skydivingrest.dto.authentication.AuthenticationResponseDTO;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerTest {

//    @Autowired
//    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // TEST Happy Path (Calling Method) | Successful Login

    @Test
    void authenticate_whenValidRequest_SuccessfulLogin() {
        User user = createValidUser();
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.saveAndFlush(user);

        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("username@test.com", "a@123456");

        ResponseEntity<AuthenticationResponseDTO> responseDTO = null;
        try {
            responseDTO = authController.authenticate(requestDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Assert

        assertEquals(HttpStatus.OK, responseDTO.getStatusCode());
        assertFalse(responseDTO.getBody().getToken().isEmpty());
        assertEquals(requestDTO.getUsername(), responseDTO.getBody().getUsername());

    }


    // TEST Invalid Login - Wrong Credentials
    @Test
    void authenticate_whenInvalidUsername_returnsUnauthorized() {
        User user = createValidUser();
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.save(user);

        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(
                "wrongUsername@test.com",
                "a@123456"
        );

        // Assert
        assertThrows(UnauthorizedException.class,
                () -> authController.authenticate(requestDTO));
    }

    @Test
    void authenticate_whenInvalidPassword_returnsUnauthorized() {
        User user = createValidUser();
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.save(user);

        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(
                "username@test.com",
                "wrong@123456"
        );

        // Assert
        assertThrows(UnauthorizedException.class,
                () -> authController.authenticate(requestDTO));
    }

    // TEST Invalid Login - Missing Credentials
    @Test
    void authenticate_whenEmptyUsername_returnsUnauthorized() {
        User user = createValidUser();
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.save(user);

        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(
                null,
                "a@123456"
        );

        // Assert
        assertThrows(UnauthorizedException.class,
                () -> authController.authenticate(requestDTO));


    }

    @Test
    void authenticate_whenEmptyPassword_returnsUnauthorized() {

            User user = createValidUser();
            user.setPassword(passwordEncoder.encode("a@123456"));
            userRepository.save(user);

            AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(
                    "username@test.com",
                    null
            );

            // Assert
            assertThrows(UnauthorizedException.class,
                    () -> authController.authenticate(requestDTO));
    }


    // Helper

    private User createValidUser(){
        User user = new User();
        user.setUsername("username@test.com");
        user.setPassword("a@123456");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");
        user.setRole(Role.SKYDIVER);
        return user;
    }

}