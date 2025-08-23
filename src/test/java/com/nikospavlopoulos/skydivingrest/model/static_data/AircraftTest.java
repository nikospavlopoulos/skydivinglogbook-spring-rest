package com.nikospavlopoulos.skydivingrest.model.static_data;

import com.nikospavlopoulos.skydivingrest.repository.AircraftRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AircraftTest {

    @Autowired
    private AircraftRepository aircraftRepository;

    // Test - Aircraft create (database persists) successfully. Retrieve by ID works.

    @Test
    void aircraftCreateTest() {
        Aircraft aircraft = new Aircraft();
        aircraft.setAircraftName("Plane1");

        Aircraft savedAircraft = aircraftRepository.saveAndFlush(aircraft);

        Aircraft foundAircraft = aircraftRepository.findById(savedAircraft.getId()).orElseThrow();

        // Asserts
        assertThat(foundAircraft.getId()).isNotNull();
        assertThat(foundAircraft.getAircraftName()).isEqualTo("Plane1");
    }

    // Test - aircraftName is not null - Validation failure

    @Test
    void aircraftValidationTest() {
        Aircraft aircraft = new Aircraft();

        aircraft.setAircraftName(null);

        //Asserts - Throws Validation Exception
        assertThrows(ConstraintViolationException.class, () -> aircraftRepository.saveAndFlush(aircraft));
    }

    // Test - aircraftName must be unique (duplicate throws exception)

    @Test
    void aircraftUniqueTest() {
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setAircraftName("Plane1");

        Aircraft savedAircraft = aircraftRepository.saveAndFlush(aircraft1);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setAircraftName("Plane1");

        // Asserts

        assertThrows(DataIntegrityViolationException.class, () -> aircraftRepository.saveAndFlush(aircraft2));
    }


}