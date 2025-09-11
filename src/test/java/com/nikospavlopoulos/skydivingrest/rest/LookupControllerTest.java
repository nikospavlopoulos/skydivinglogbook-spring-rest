package com.nikospavlopoulos.skydivingrest.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.*;
import com.nikospavlopoulos.skydivingrest.security.jwt.IJwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class LookupControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JumpRepository jumpRepository;
    @Autowired
    private AircraftRepository aircraftRepository;
    @Autowired
    private JumptypeRepository jumptypeRepository;
    @Autowired
    private DropzoneRepository dropzoneRepository;
    @Autowired
    private IJwtService jwtService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;




    // GET - All Aircraft
    @Test
    void getAllAircraft_whenSuccessful_Return200() throws Exception {
        Aircraft aircraft = persistAircraft();

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        mockMvc.perform(get("/api/lookups/aircraft")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }


    // GET - All Dropzones
    @Test
    void getAllDropzones_whenSuccessful_Return200() throws Exception {
        Dropzone dropzone = persistDropzone();

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        mockMvc.perform(get("/api/lookups/dropzones")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // GET - All Jumptypes
    @Test
    void getAllJumptypes_whenSuccessful_Return200() throws Exception {

        Jumptype jumptype = persistJumptype();

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        mockMvc.perform(get("/api/lookups/jumptypes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Helpers


    private Aircraft jumpAircraft() {
        Aircraft aircraft = new Aircraft();
        aircraft.setAircraftName("Cessna");
        return aircraft;
    }

    Aircraft persistAircraft () {
        return aircraftRepository.saveAndFlush(jumpAircraft());
    }

    private Dropzone jumpDropzone() {
        Dropzone dropzone = new Dropzone();
        dropzone.setDropzoneName("Athens");
        return dropzone;
    }

    Dropzone persistDropzone() {
        return dropzoneRepository.saveAndFlush(jumpDropzone());
    }

    private Jumptype jumpJumptype() {
        Jumptype jumptype = new Jumptype();
        jumptype.setJumptypeName("Belly");
        return jumptype;
    }

    Jumptype persistJumptype() {
        return jumptypeRepository.saveAndFlush(jumpJumptype());
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


}