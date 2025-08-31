package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.DropzoneLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DropzoneMapperTest {

    @Autowired
    private DropzoneMapper dropzoneMapper;

    // Test - Happy path | All fields populated correctly
    @Test
    void dropzoneMapperSuccess() {
        Dropzone dropzone = createValidDropzone();

        DropzoneLookupDTO dto = dropzoneMapper.dropzoneToDropzoneLookupDTO(dropzone);

        assertEquals(1L, dto.getId());
        assertEquals("Cessna", dto.getDropzoneName());
    }

    // Test - Whole Entity is Null

    @Test
    void dropzoneEntityNullFail() {
        DropzoneLookupDTO dto = dropzoneMapper.dropzoneToDropzoneLookupDTO(null);

        assertNull(dto);
    }


    // Test - Field in Entity is Null
    @ParameterizedTest
    @ValueSource(strings = {"id", "dropzoneName"})
    void dropzoneFieldNullFail(String field) {

        Dropzone dropzone = createValidDropzone();


        switch (field) {
            case "id":{
                dropzone.setId(null);
                DropzoneLookupDTO dto = dropzoneMapper.dropzoneToDropzoneLookupDTO(dropzone);
                assertNull(dto.getId());
                break;}
            case "dropzoneName":{
                dropzone.setDropzoneName(null);
                DropzoneLookupDTO dto = dropzoneMapper.dropzoneToDropzoneLookupDTO(dropzone);
                assertNull(dto.getDropzoneName());
                break;}
        }
    }


    // Helper functions

    private Dropzone createValidDropzone() {
        return new Dropzone(1L, "Cessna");
    }

    private Dropzone createNullEntityDropzone() {
        return new Dropzone();
    }

}