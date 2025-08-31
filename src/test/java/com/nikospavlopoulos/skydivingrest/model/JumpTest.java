package com.nikospavlopoulos.skydivingrest.model;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class JumpTest {

    @Autowired
    private JumpRepository jumpRepository;
    @Autowired
    private AircraftRepository aircraftRepository;
    @Autowired
    private DropzoneRepository dropzoneRepository;
    @Autowired
    private JumptypeRepository jumptypeRepository;
    @Autowired
    private UserRepository userRepository;

    // Test Verify Successfull Create of a jump, and successful persistance in DB

    @Test
    void jumpCreationTest() {
        Jump jump = createValidJump();

        Jump savedJump = jumpRepository.saveAndFlush(jump);

        Jump foundJump = jumpRepository.findById(savedJump.getId()).orElseThrow();

        //Assert
        assertNotNull(foundJump.getId());
        assertNotNull(foundJump.getUuid());
        assertEquals(10000, foundJump.getAltitude());
        assertEquals(50, foundJump.getFreeFallDuration());
        assertEquals(LocalDate.now().atStartOfDay(), foundJump.getJumpDate());
        assertEquals("Lorem", foundJump.getJumpNotes());
        assertEquals("Cessna", foundJump.getAircraft().getAircraftName());
        assertEquals("Athens", foundJump.getDropzone().getDropzoneName());
        assertEquals("Belly", foundJump.getJumptype().getJumptypeName());
        assertEquals("user@user.com", foundJump.getUser().getUsername());
    }

    // Test not nullable values - Throw Exception DataIntegrityViolationException

    @ParameterizedTest
    @ValueSource(strings = {"altitude", "freeFallDuration", "jumpDate", "aircraft", "dropzone", "jumptype"})
    void jumpValidationTest(String field) {
        Jump jump = createValidJump();

        switch (field) {
            case "altitude":
                jump.setAltitude(null);
                break;
            case "freeFallDuration":
                jump.setFreeFallDuration(null);
                break;
            case "jumpDate":
                jump.setJumpDate(null);
                break;
            case "aircraft":
                jump.setAircraft(null);
                break;
            case "dropzone":
                jump.setDropzone(null);
                break;
            case "jumptype":
                jump.setJumptype(null);
                break;
        }

        assertThrows(DataIntegrityViolationException.class, () -> jumpRepository.saveAndFlush(jump));
    }

    // Test Constraint Values. Altitude > 0, Freefall duration > 0, Jump Date must not be future date

    @ParameterizedTest
    @ValueSource(strings = {"altitude", "freeFallDuration", "jumpDate"})
    void jumpConstraintTest(String field) {
        Jump jump = createValidJump();

        switch (field) {
            case "altitude":
                jump.setAltitude(-5000);
                break;
            case "freeFallDuration":
                jump.setFreeFallDuration(-20);
                break;
            case "jumpDate":
                jump.setJumpDate(LocalDate.now().atStartOfDay().plusDays(2));
                break;
        }

        assertThrows(ConstraintViolationException.class, () -> jumpRepository.saveAndFlush(jump));
    }

    // test UUID generation
    @Test
    void generateUUIDTest() {
        Jump jump = createValidJump();
        jumpRepository.saveAndFlush(jump);

        assertNotNull(jump.getUuid());
    }
    @Test
    void generateUUIDTestNotNull() {
        Jump jump = createValidJump();

        UUID notNullUuid = UUID.randomUUID();
        jump.setUuid(notNullUuid);

        jumpRepository.saveAndFlush(jump);

        assertEquals(notNullUuid, jump.getUuid());
    }


    // Helper functions

    private User jumpUser() {
        User jumpUser = new User();
        jumpUser.setUsername("user@user.com");
        jumpUser.setPassword("12345");
        jumpUser.setFirstname("Firstname");
        jumpUser.setLastname("Lastname");
        jumpUser.setRole(Role.SKYDIVER);
        return jumpUser;
    }

    User persistUser () {
        return userRepository.saveAndFlush(jumpUser());
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

    private Jump createValidJump() {
        return Jump.builder()
                .altitude(10000)
                .freeFallDuration(50)
                .jumpDate(LocalDate.now().atStartOfDay())
                .jumpNotes("Lorem")
                .aircraft(persistAircraft())
                .dropzone(persistDropzone())
                .jumptype(persistJumptype())
                .user(persistUser())
                .build();
    }
}