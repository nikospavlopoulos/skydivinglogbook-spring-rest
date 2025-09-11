package com.nikospavlopoulos.skydivingrest.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.JumpUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.*;
import com.nikospavlopoulos.skydivingrest.security.jwt.IJwtService;
import com.nikospavlopoulos.skydivingrest.service.IJumpService;
import com.nikospavlopoulos.skydivingrest.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthorizeCallback;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class JumpControllerTest {


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

    // Test POST /api/jumps (Create Jump)
    @Test
    void createJump_whenSuccessful_Return201() throws Exception {
        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        JumpInsertDTO newJumpInsertDto = createValidjumpInsertDTO();
        String requestBody = objectMapper.writeValueAsString(newJumpInsertDto);

       MvcResult result = mockMvc.perform(post("/api/jumps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isCreated())
                .andReturn();

       JumpLookupDTO createdJump = objectMapper.readValue(result.getResponse().getContentAsString(), JumpLookupDTO.class);

        assertEquals(newJumpInsertDto.getAltitude(), createdJump.getAltitude());
        assertEquals(newJumpInsertDto.getDropzoneId(), createdJump.getDropzone().getId());
    }

    // GET /api/jumps/{id} (Get Single Jump)
    @Test
    void getJump_whenSuccessful_Return200() throws Exception {

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        Jump jump = createValidJump();
        jump.setUser(newUser);
        jumpRepository.save(jump);

       MvcResult retrievedJump = mockMvc.perform(get("/api/jumps/{id}", jump.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

       // Assert Jump Values
        JumpLookupDTO returnedJump = objectMapper.readValue(retrievedJump.getResponse().getContentAsString(), JumpLookupDTO.class);

        assertEquals(jump.getId(), returnedJump.getId());
        assertEquals(jump.getUuid(), returnedJump.getUuid());
    }


    // PUT /api/jumps/{id} (Update Jump)
    @Test
    void updateJump_whenSuccessful_returns200() throws Exception {

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        Jump jump = createValidJump();
        jump.setUser(newUser);
        jumpRepository.save(jump);

        JumpUpdateDTO newJumpUpdateDto = new JumpUpdateDTO(
                15000, 80, LocalDateTime.now().toLocalDate().atStartOfDay(), "Update Notes", jump.getAircraft().getId(), jump.getDropzone().getId(), jump.getJumptype().getId()
        );
        String requestBody = objectMapper.writeValueAsString(newJumpUpdateDto);

        MvcResult result = mockMvc.perform(put("/api/jumps/{id}", jump.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        JumpLookupDTO updatedJump = objectMapper.readValue(result.getResponse().getContentAsString(), JumpLookupDTO.class);

        assertEquals(jump.getUuid(), updatedJump.getUuid());
        assertEquals(newJumpUpdateDto.getAltitude(), updatedJump.getAltitude());
        assertEquals(newJumpUpdateDto.getJumpNotes(), updatedJump.getJumpNotes());
    }


    // DELETE /api/jumps/{id} (Delete Jump)
    @Test
    void deleteJump_whenSuccessful_Return200() throws Exception{

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        Jump jump = createValidJump();
        jump.setUser(newUser);
        jumpRepository.save(jump);

        mockMvc.perform(delete("/api/jumps/{id}", jump.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk());

        Optional<Jump> deletedJump = jumpRepository.findById(jump.getId());

        assertTrue(deletedJump.isEmpty());
    }


    // GET /api/jumps/all (Paginated List)
    @Test
    void getAllJumps_whenSuccesfull_Returns200() throws Exception {

        Aircraft aircraft = persistAircraft();
        Dropzone dropzone = persistDropzone();
        Jumptype jumptype = persistJumptype();

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        List<Jump> allJumps = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            Jump jump = new Jump(
                    null, UUID.randomUUID(), 10000, 50, LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(i),
                    aircraft, dropzone, jumptype, newUser,
                    String.format(" %d: Jumpnotes", i));
            allJumps.add(jump);
        }
        jumpRepository.saveAll(allJumps);

        MvcResult result = mockMvc.perform(get("/api/jumps/all")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andReturn();



    }

    // GET /api/jumps/totalfreefall
    @Test
    void gettotalFreefal_whenSuccessful_return200() throws Exception {
        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        Jump jump = createValidJump();
        jump.setUser(newUser);
        jumpRepository.save(jump);

        MvcResult result = mockMvc.perform(get("/api/jumps/totalfreefall")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("50"));
    }


    // GET /api/jumps/totaljumps
    @Test
    void gettotaljumps_whenSuccessful_returns200() throws Exception {
        Aircraft aircraft = persistAircraft();
        Dropzone dropzone = persistDropzone();
        Jumptype jumptype = persistJumptype();

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        List<Jump> allJumps = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            Jump jump = new Jump(
                    null, UUID.randomUUID(), 10000, 50, LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(i),
                    aircraft, dropzone, jumptype, newUser,
                    String.format(" %d: Jumpnotes", i));
            allJumps.add(jump);
        }
        jumpRepository.saveAll(allJumps);

        MvcResult result = mockMvc.perform(get("/api/jumps/totaljumps")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("10"));
    }

    // GET /api/jumps/search (Paginated Search)
    @Test
    void getJumpSearch_whenSucecssful_Returns200() throws Exception {
        Aircraft aircraft = persistAircraft();
        Dropzone dropzone = persistDropzone();
        Jumptype jumptype = persistJumptype();

        User newUser = userRepository.saveAndFlush(createValidUser());
        String userToken = jwtService.generateToken(newUser.getUsername(), newUser.getRole().toString());

        List<Jump> allJumps = new ArrayList<>();
        for (int i = 0; i < 7 ; i++) {
            Jump jump = new Jump(
                    null, UUID.randomUUID(), 10000, 50, LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(i),
                    aircraft, dropzone, jumptype, newUser,
                    String.format(" %d: Jumpnotes", i));
            allJumps.add(jump);
        }
        jumpRepository.saveAll(allJumps);

        // Search parameters
        String jumpDateFrom = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(8).toString();
        String jumpDateTo = LocalDateTime.now().toLocalDate().atStartOfDay().toString();

        mockMvc.perform(get("/api/jumps/search")
                .param("jumpDateFrom", jumpDateFrom)
                .param("jumpDateTo", jumpDateTo)
                .param("jumptype", jumptype.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(7));
    }


    // Helpers

    private JumpInsertDTO createValidjumpInsertDTO() {
        return new JumpInsertDTO(
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                "Test Notes",
                persistAircraft().getId(),
                persistDropzone().getId(),
                persistJumptype().getId(),
                createValidUser().getId()
        );
    }

    private Jump createValidJump() {
        return new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                persistAircraft(),
                persistDropzone(),
                persistJumptype(),
                createValidUser(),
                "Test Notes"
        );
    }

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