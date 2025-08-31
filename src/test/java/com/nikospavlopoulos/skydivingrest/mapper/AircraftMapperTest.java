package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.dto.lookup.AircraftLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
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
class AircraftMapperTest {

    @Autowired
    private AircraftMapper aircraftMapper;

    // Test - Happy path | All fields populated correctly
    @Test
    void aircraftMapperSuccess() {
        Aircraft aircraft = createValidAircraft();

        AircraftLookupDTO dto = aircraftMapper.aircraftToLookupDTO(aircraft);

        assertEquals(1L, dto.getId());
        assertEquals("Cessna", dto.getAircraftName());
    }

    // Test - Whole Entity is Null

    @Test
    void aircraftEntityNullFail() {
        AircraftLookupDTO dto = aircraftMapper.aircraftToLookupDTO(null);

        assertNull(dto);
    }


    // Test - Field in Entity is Null
    @ParameterizedTest
    @ValueSource(strings = {"id", "aircraftName"})
    void aircraftFieldNullFail(String field) {

        Aircraft aircraft = createValidAircraft();


        switch (field) {
            case "id":{
                aircraft.setId(null);
                AircraftLookupDTO dto = aircraftMapper.aircraftToLookupDTO(aircraft);
                assertNull(dto.getId());
                break;}
            case "aircraftName":{
                aircraft.setAircraftName(null);
                AircraftLookupDTO dto = aircraftMapper.aircraftToLookupDTO(aircraft);
                assertNull(dto.getAircraftName());
                break;}
        }
    }


    // Helper functions

    private Aircraft createValidAircraft() {
        return new Aircraft(1L, "Cessna");
    }

    private Aircraft createNullEntityAircraft() {
        return new Aircraft();
    }

}