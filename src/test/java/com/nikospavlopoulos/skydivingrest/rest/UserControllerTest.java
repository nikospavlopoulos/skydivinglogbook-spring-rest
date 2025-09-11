package com.nikospavlopoulos.skydivingrest.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.dto.PasswordUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserUpdateDTO;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import com.nikospavlopoulos.skydivingrest.security.jwt.IJwtService;
import com.nikospavlopoulos.skydivingrest.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IJwtService jwtService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Test POST - /api/users - Register User
    @Test
    void registerUser_WhenSuccessFull_Returns201Created() throws Exception {

        UserInsertDTO newUserInsertDto = createValidUserInsertDto();

        String requestBody = objectMapper.writeValueAsString(newUserInsertDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.username").value(newUserInsertDto.getUsername()));

        Optional<User> createdUser = userRepository.findByUsername(newUserInsertDto.getUsername());

        assertTrue(createdUser.isPresent());
        assertEquals(newUserInsertDto.getLastname(), createdUser.get().getLastname());
        assertEquals(Role.SKYDIVER, createdUser.get().getRole());
    }


    // Test GET - /api/users/{id} - Retrieve User Profile
    @Test
    void getUser_WhenSuccessFull_Returns200() throws Exception {

        User newUser = userRepository.saveAndFlush(createValidUser());
         String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        User newAdmin = userRepository.saveAndFlush(createValidAdmin());
        String adminToken = jwtService.generateToken(newAdmin.getUsername(), newAdmin.getRole().toString());


        mockMvc.perform(get("/api/users/{id}", newUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{id}", newAdmin.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());


        mockMvc.perform(get("/api/users/{id}", newUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

// Test PUT /api/users/{id} - Update User Info
    @Test
    void updateUser_whenSuccessful_Returns200() throws Exception {
        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        User newAdmin = userRepository.saveAndFlush(createValidAdmin());
        String adminToken = jwtService.generateToken(newAdmin.getUsername(), newAdmin.getRole().toString());

        UserUpdateDTO updatedUser = userValidUpdateDTO();
        String requestBody = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(put("/api/users/{id}", newUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/users/{id}", newUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        Optional<User> retrieveUpdatedUser = userRepository.findById(newUser.getId());

        assertEquals(retrieveUpdatedUser.get().getUsername(), updatedUser.getUsername());
        assertEquals(retrieveUpdatedUser.get().getLastname(), updatedUser.getLastname());
    }

// Test - PUT /users/{id}/password - Change Password
    @Test
    void changePassword_whenSuccessful_Returns200() throws Exception {


        User newUser = userRepository.saveAndFlush(createValidUser());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        User newAdmin = userRepository.saveAndFlush(createValidAdmin());
        String adminToken = jwtService.generateToken(newAdmin.getUsername(), newAdmin.getRole().toString());

        PasswordUpdateDTO updatedPassword = passwordValidUpdateDTO();

        String requestBody = objectMapper.writeValueAsString(updatedPassword);

        mockMvc.perform(put("/api/users/{id}/password", newUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());


        mockMvc.perform(put("/api/users/{id}/password", newUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

    }


// Test - PUT /api/users/{id} - Deactivate User
    @Test
    void deactivateUser_whenSuccessful_returns200() throws Exception {

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        User newAdmin = userRepository.saveAndFlush(createValidAdmin());
        String adminToken = jwtService.generateToken(newAdmin.getUsername(), newAdmin.getRole().toString());

        mockMvc.perform(delete("/api/users/{id}", newUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/users/{id}", newUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }


// Test - GET - /api/users/all - List All Users (ADMIN only)
    @Test
    void listAllUsers_whenAdmin_Return200() throws Exception {
        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        User newAdmin = userRepository.saveAndFlush(createValidAdmin());
        String adminToken = jwtService.generateToken(newAdmin.getUsername(), newAdmin.getRole().toString());

        mockMvc.perform(get("/api/users/all")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/users/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    // Helpers

    private PasswordUpdateDTO passwordValidUpdateDTO() {
        return new PasswordUpdateDTO(
                "a@123456",
                "a@456789"
        );
    }

    private UserUpdateDTO userValidUpdateDTO() {
        UserUpdateDTO updateUser = new UserUpdateDTO();
        updateUser.setUsername("updatedusername@test.com");
        updateUser.setFirstname("updatedFirstname");
        updateUser.setLastname("updatedLastname");
        return updateUser;
    }

    private UserInsertDTO createValidUserInsertDto(){
        UserInsertDTO user = new UserInsertDTO();
        user.setUsername("username@test.com");
        user.setPassword("a@123456");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");
        return user;
    }

    private User createValidUser(){
        User user = new User();
        user.setUsername("username@test.com");
        user.setPassword("a@123456");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");
        user.setRole(Role.SKYDIVER);
        return user;
    }

    private User createValidAdmin(){
        User user = new User();
        user.setUsername("adminusername@test.com");
        user.setPassword("a@123456");
        user.setFirstname("AdminFirstname");
        user.setLastname("AdminLastname");
        user.setRole(Role.ADMIN);
        return user;
    }

}