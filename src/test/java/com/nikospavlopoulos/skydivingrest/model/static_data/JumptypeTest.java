package com.nikospavlopoulos.skydivingrest.model.static_data;

import com.nikospavlopoulos.skydivingrest.repository.JumptypeRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class JumptypeTest {


    @Autowired
    private JumptypeRepository jumptypeRepository;

    // Test - Jumptype create (database persists) successfully. Retrieve by ID works.

    @Test
    void jumptypeCreateTest() {
        Jumptype jumptype = new Jumptype();
        jumptype.setJumptypeName("Zone1");

        Jumptype savedJumptype = jumptypeRepository.saveAndFlush(jumptype);

        Jumptype foundJumptype = jumptypeRepository.findById(savedJumptype.getId()).orElseThrow();

        // Asserts
        assertThat(foundJumptype.getId()).isNotNull();
        assertThat(foundJumptype.getJumptypeName()).isEqualTo("Zone1");
    }

    // Test - jumptypeName is not null - Validation failure

    @Test
    void jumptypeValidationTest() {
        Jumptype jumptype = new Jumptype();

        jumptype.setJumptypeName(null);

        //Asserts - Throws Validation Exception
        assertThrows(ConstraintViolationException.class, () -> jumptypeRepository.saveAndFlush(jumptype));
    }

    // Test - jumptypeName must be unique (duplicate throws exception)

    @Test
    void jumptypeUniqueTest() {
        Jumptype jumptype1 = new Jumptype();
        jumptype1.setJumptypeName("Zone1");

        Jumptype savedJumptype = jumptypeRepository.saveAndFlush(jumptype1);

        Jumptype jumptype2 = new Jumptype();
        jumptype2.setJumptypeName("Zone1");

        // Asserts

        assertThrows(DataIntegrityViolationException.class, () -> jumptypeRepository.saveAndFlush(jumptype2));
    }

}