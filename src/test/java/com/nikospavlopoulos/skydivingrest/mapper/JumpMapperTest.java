package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JumpMapperTest {

    @Autowired
    private JumpMapper jumpMapper;

    // == TEST JumpInsertDTO to Jump Entity == //
    // toEntityTest - Happy Path | All fields populated correctly
    @Test
    void jumpInsertToJumpEntitySuccess() {
        JumpInsertDTO jumpInsertDTO = createValidJumpInsert();

        Jump dto = jumpMapper.jumpInsertDTOtoJumpEntity(jumpInsertDTO);

        assertEquals(10000, dto.getAltitude());
        assertEquals(60, dto.getFreeFallDuration());
        assertEquals(LocalDateTime.now().toLocalDate().atStartOfDay(), dto.getJumpDate());
        assertEquals("Notes", dto.getJumpNotes());
    }


    // Helper function
    private JumpInsertDTO createValidJumpInsert() {
        return new JumpInsertDTO(
                10000,
                60,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                "Notes",
                1L,
                1L,
                1L,
                1L
        );
    }


    // == TEST Jump Entity to JumpLookupDTO == //
    // toLookupTest - Happy path | All fields populated correctly
    @Test
    void toJumpLookupMapperSuccess() {
        Jump jump = createValidJump();

        JumpLookupDTO dto = jumpMapper.jumpToJumpLookupDTO(jump);

        assertEquals(1L, dto.getId());
        assertEquals(jump.getUuid(), dto.getUuid());
        assertEquals(10000, dto.getAltitude());
        assertEquals(60, dto.getFreeFallDuration());
        assertEquals(LocalDateTime.now().toLocalDate().atStartOfDay(), dto.getJumpDate());
        assertEquals("Notes", dto.getJumpNotes());
        assertEquals("Cessna", dto.getAircraft().getAircraftName());
        assertEquals("Athens", dto.getDropzone().getDropzoneName());
        assertEquals("Belly", dto.getJumptype().getJumptypeName());
        assertEquals("test@test.com", dto.getUser().getUsername());
    }

    // toLookupTest - Whole Entity is Null
    @Test
    void toJumpLookupEntityNullFail() {
        JumpLookupDTO dto = jumpMapper.jumpToJumpLookupDTO(null);

        assertNull(dto);
    }


    // Helper functions
    private Aircraft aircraft = new Aircraft(1L, "Cessna");

    private Dropzone dropzone = new Dropzone(1L, "Athens");

    private Jumptype jumptype = new Jumptype(1L, "Belly");

    private User user = new User(1L, UUID.randomUUID(),Boolean.TRUE, "test@test.com", "a@123456", "Nikos", "Test", Role.SKYDIVER);

    private Jump createValidJump() {
        Jump jump = new Jump();
            jump.setId(1L);
            jump.setUuid(UUID.randomUUID());
            jump.setAltitude(10000);
            jump.setFreeFallDuration(60);
            jump.setJumpDate(LocalDateTime.now().toLocalDate().atStartOfDay());
            jump.setJumpNotes("Notes");
            jump.setAircraft(aircraft);
            jump.setDropzone(dropzone);
            jump.setJumptype(jumptype);
            jump.setUser(user);
            return jump;
    }

    private Jump createNullEntityJump() {
        return new Jump();
    }
}