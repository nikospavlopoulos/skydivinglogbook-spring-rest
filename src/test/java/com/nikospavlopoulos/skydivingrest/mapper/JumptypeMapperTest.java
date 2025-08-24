package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.JumptypeLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class JumptypeMapperTest {

    @Autowired
    private JumptypeMapper jumptypeMapper;

    // Test - Happy path | All fields populated correctly
    @Test
    void jumptypeMapperSuccess() {
        Jumptype jumptype = createValidJumptype();

        JumptypeLookupDTO dto = jumptypeMapper.jumptypeToJumptypeLookupDTO(jumptype);

        assertEquals(1L, dto.getId());
        assertEquals("Cessna", dto.getJumptypeName());
    }

    // Test - Whole Entity is Null

    @Test
    void jumptypeEntityNullFail() {
        JumptypeLookupDTO dto = jumptypeMapper.jumptypeToJumptypeLookupDTO(null);

        assertNull(dto);
    }


    // Test - Field in Entity is Null
    @ParameterizedTest
    @ValueSource(strings = {"id", "jumptypeName"})
    void jumptypeFieldNullFail(String field) {

        Jumptype jumptype = createValidJumptype();


        switch (field) {
            case "id":{
                jumptype.setId(null);
                JumptypeLookupDTO dto = jumptypeMapper.jumptypeToJumptypeLookupDTO(jumptype);
                assertNull(dto.getId());
                break;}
            case "jumptypeName":{
                jumptype.setJumptypeName(null);
                JumptypeLookupDTO dto = jumptypeMapper.jumptypeToJumptypeLookupDTO(jumptype);
                assertNull(dto.getJumptypeName());
                break;}
        }
    }


    // Helper functions

    private Jumptype createValidJumptype() {
        return new Jumptype(1L, "Cessna");
    }

    private Jumptype createNullEntityJumptype() {
        return new Jumptype();
    }


}