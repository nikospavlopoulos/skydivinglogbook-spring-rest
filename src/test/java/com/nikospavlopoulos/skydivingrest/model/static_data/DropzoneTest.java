package com.nikospavlopoulos.skydivingrest.model.static_data;

import com.nikospavlopoulos.skydivingrest.repository.DropzoneRepository;
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
class DropzoneTest {

    @Autowired
    private DropzoneRepository dropzoneRepository;

    // Test - Dropzone create (database persists) successfully. Retrieve by ID works.

    @Test
    void dropzoneCreateTest() {
        Dropzone dropzone = new Dropzone();
        dropzone.setDropzoneName("Zone1");

        Dropzone savedDropzone = dropzoneRepository.saveAndFlush(dropzone);

        Dropzone foundDropzone = dropzoneRepository.findById(savedDropzone.getId()).orElseThrow();

        // Asserts
        assertThat(foundDropzone.getId()).isNotNull();
        assertThat(foundDropzone.getDropzoneName()).isEqualTo("Zone1");
    }

    // Test - dropzoneName is not null - Validation failure

    @Test
    void dropzoneValidationTest() {
        Dropzone dropzone = new Dropzone();

        dropzone.setDropzoneName(null);

        //Asserts - Throws Validation Exception
        assertThrows(ConstraintViolationException.class, () -> dropzoneRepository.saveAndFlush(dropzone));
    }

    // Test - dropzoneName must be unique (duplicate throws exception)

    @Test
    void dropzoneUniqueTest() {
        Dropzone dropzone1 = new Dropzone();
        dropzone1.setDropzoneName("Zone1");

        Dropzone savedDropzone = dropzoneRepository.saveAndFlush(dropzone1);

        Dropzone dropzone2 = new Dropzone();
        dropzone2.setDropzoneName("Zone1");

        // Asserts

        assertThrows(DataIntegrityViolationException.class, () -> dropzoneRepository.saveAndFlush(dropzone2));
    }
    
}