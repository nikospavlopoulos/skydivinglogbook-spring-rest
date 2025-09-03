package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ResourceNotFoundException;
import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.JumpUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.mapper.JumpMapper;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JumpServiceImplTest {

    @Autowired
    private JumpServiceImpl jumpService;
    @Autowired
    private JumpRepository jumpRepository;
    @Autowired
    private JumpMapper jumpMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AircraftRepository aircraftRepository;
    @Autowired
    private DropzoneRepository dropzoneRepository;
    @Autowired
    private JumptypeRepository jumptypeRepository;
    @Autowired
    private JumpServiceImpl jumpServiceImpl;


// CREATE USER TESTS

    // TODO: Refactor when Authorization confirm Owner - or throw UnauthorizedException (or AccessDeniedException for @PreAuthorize)

    ///  Happy Path - Jump Insert and DB Persistence works as expected
    @Test
    void createJump_whenValiDTO_shouldSaveAndReturnJumpLookupDTO() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        JumpInsertDTO jumpInsertDTO = createValidJumpInsertDTO();
        jumpInsertDTO.setUserId(user.getId());
        jumpInsertDTO.setDropzoneId(dropzone.getId());
        jumpInsertDTO.setAircraftId(aircraft.getId());
        jumpInsertDTO.setJumptypeId(jumptype.getId());

        JumpLookupDTO savedJump = jumpService.createJump(jumpInsertDTO);

        // Assert
        assertEquals(user.getId(), savedJump.getUser().getId());
        assertEquals(10000, savedJump.getAltitude());
        assertEquals(50, savedJump.getFreeFallDuration());
        assertEquals(LocalDateTime.now().toLocalDate().atStartOfDay(), savedJump.getJumpDate());
        assertTrue(savedJump.getJumpNotes().contains("Notes"));
        assertEquals(aircraft.getId(), savedJump.getAircraft().getId());
        assertEquals(dropzone.getId(), savedJump.getDropzone().getId());
        assertEquals(jumptype.getId(), savedJump.getJumptype().getId());
    }

    /// User Not Found - throws ResourceNotFoundException
    @Test
    void createJump_whenUserMissing_shouldThrowResourceNotFoundException() {
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        JumpInsertDTO jumpInsertDTO = createValidJumpInsertDTO();
        jumpInsertDTO.setUserId(Long.MAX_VALUE);
        jumpInsertDTO.setDropzoneId(dropzone.getId());
        jumpInsertDTO.setAircraftId(aircraft.getId());
        jumpInsertDTO.setJumptypeId(jumptype.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.createJump(jumpInsertDTO));
    }

    /// Dropzone Not Found - throws ResourceNotFoundException
    @Test
    void createJump_whenDropzoneMissing_shouldThrowResourceNotFoundException() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());

        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        JumpInsertDTO jumpInsertDTO = createValidJumpInsertDTO();
        jumpInsertDTO.setUserId(user.getId());
        jumpInsertDTO.setDropzoneId(Long.MAX_VALUE);
        jumpInsertDTO.setAircraftId(aircraft.getId());
        jumpInsertDTO.setJumptypeId(jumptype.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.createJump(jumpInsertDTO));
    }

    /// Aircraft Not Found - throws ResourceNotFoundException
    @Test
    void createJump_whenAircraftMissing_shouldThrowResourceNotFoundException() {
        User user = userRepository.save(createValidUser());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        JumpInsertDTO jumpInsertDTO = createValidJumpInsertDTO();
        jumpInsertDTO.setUserId(user.getId());
        jumpInsertDTO.setDropzoneId(dropzone.getId());
        jumpInsertDTO.setAircraftId(Long.MAX_VALUE);
        jumpInsertDTO.setJumptypeId(jumptype.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.createJump(jumpInsertDTO));
    }

    /// Jumptype Not Found - throws ResourceNotFoundException
    @Test
    void createJump_whenJumptypeMissing_shouldThrowResourceNotFoundException() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());

        JumpInsertDTO jumpInsertDTO = createValidJumpInsertDTO();
        jumpInsertDTO.setUserId(user.getId());
        jumpInsertDTO.setDropzoneId(dropzone.getId());
        jumpInsertDTO.setAircraftId(aircraft.getId());
        jumpInsertDTO.setJumptypeId(Long.MAX_VALUE);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.createJump(jumpInsertDTO));
    }



// UPDATE JUMP TESTS

    // TODO: Refactor when Authorization confirm Owner - or throw UnauthorizedException (or AccessDeniedException for @PreAuthorize)

    /// Happy Path - Jump Update and DB Persistence works as expected
    @Test
    void updateJump_whenValiDTO_shouldSaveAndReturnJumpLookupDTO() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        Jump existingJump = new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                aircraft,
                dropzone,
                jumptype,
                user,
                "Test Notes"
        );

        jumpRepository.save(existingJump);

        JumpUpdateDTO jumpUpdateDTO = createValidJumpUpdateDTO();
        jumpUpdateDTO.setAircraftId(aircraft.getId());
        jumpUpdateDTO.setDropzoneId(dropzone.getId());
        jumpUpdateDTO.setJumptypeId(jumptype.getId());

        jumpService.updateJump(existingJump.getId(), jumpUpdateDTO);

        JumpLookupDTO updatedLookupDtoJump = jumpService.updateJump(existingJump.getId(), jumpUpdateDTO);

        // Assert the returned JumpLookUp DTO
        assertEquals(12000 ,updatedLookupDtoJump.getAltitude());
        assertEquals(60, updatedLookupDtoJump.getFreeFallDuration());
        assertEquals(LocalDateTime.now().toLocalDate().atStartOfDay(), updatedLookupDtoJump.getJumpDate());
        assertTrue(updatedLookupDtoJump.getJumpNotes().contains("Update"));
        assertEquals(aircraft.getId(), updatedLookupDtoJump.getAircraft().getId());
        assertEquals(dropzone.getId(), updatedLookupDtoJump.getDropzone().getId());
        assertEquals(jumptype.getId(), updatedLookupDtoJump.getJumptype().getId());

        Jump updatedEntityJump = jumpRepository.findById(existingJump.getId()).orElseThrow();

        // Assert the returned Entity
        assertEquals(12000 ,updatedEntityJump.getAltitude());
        assertEquals(60, updatedEntityJump.getFreeFallDuration());
        assertEquals(LocalDateTime.now().toLocalDate().atStartOfDay(), updatedEntityJump.getJumpDate());
        assertTrue(updatedEntityJump.getJumpNotes().contains("Update"));
        assertEquals(aircraft.getId(), updatedEntityJump.getAircraft().getId());
        assertEquals(dropzone.getId(), updatedEntityJump.getDropzone().getId());
        assertEquals(jumptype.getId(), updatedEntityJump.getJumptype().getId());
    }

    /// existing Jump Not Found - throws ResourceNotFoundException
    @Test
    void updateJump_whenJumpMissing_shouldThrowResourceNotFoundException() {

        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        JumpUpdateDTO jumpUpdateDTO = createValidJumpUpdateDTO();
        jumpUpdateDTO.setAircraftId(aircraft.getId());
        jumpUpdateDTO.setDropzoneId(dropzone.getId());
        jumpUpdateDTO.setJumptypeId(jumptype.getId());


        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.updateJump(Long.MAX_VALUE, jumpUpdateDTO));

    }



    /// Dropzone Not Found - throws ResourceNotFoundException
    @Test
    void updateJump_whenDropzoneMissing_shouldThrowResourceNotFoundException() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        Jump existingJump = new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                aircraft,
                dropzone,
                jumptype,
                user,
                "Test Notes"
        );

        jumpRepository.save(existingJump);

        JumpUpdateDTO jumpUpdateDTO = createValidJumpUpdateDTO();
        jumpUpdateDTO.setAircraftId(aircraft.getId());
        jumpUpdateDTO.setDropzoneId(Long.MAX_VALUE);
        jumpUpdateDTO.setJumptypeId(jumptype.getId());

//        jumpService.updateJump(existingJump.getId(), jumpUpdateDTO);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.updateJump(existingJump.getId(), jumpUpdateDTO));


    }



    /// Aircraft Not Found - throws ResourceNotFoundException
    @Test
    void updateJump_whenAircraftMissing_shouldThrowResourceNotFoundException() {

        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        Jump existingJump = new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                aircraft,
                dropzone,
                jumptype,
                user,
                "Test Notes"
        );

        jumpRepository.save(existingJump);

        JumpUpdateDTO jumpUpdateDTO = createValidJumpUpdateDTO();
        jumpUpdateDTO.setAircraftId(Long.MAX_VALUE);
        jumpUpdateDTO.setDropzoneId(dropzone.getId());
        jumpUpdateDTO.setJumptypeId(jumptype.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.updateJump(existingJump.getId(), jumpUpdateDTO));

    }


    /// Jumptype Not Found - throws ResourceNotFoundException
    @Test
    void updateJump_whenJumptypeMissing_shouldThrowResourceNotFoundException() {

        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        Jump existingJump = new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                aircraft,
                dropzone,
                jumptype,
                user,
                "Test Notes"
        );

        jumpRepository.save(existingJump);

        JumpUpdateDTO jumpUpdateDTO = createValidJumpUpdateDTO();
        jumpUpdateDTO.setAircraftId(aircraft.getId());
        jumpUpdateDTO.setDropzoneId(dropzone.getId());
        jumpUpdateDTO.setJumptypeId(Long.MAX_VALUE);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> jumpService.updateJump(existingJump.getId(), jumpUpdateDTO));
    }


// DELETE JUMP TESTS
    // TODO: Refactor when Authorization confirm Owner - or throw UnauthorizedException (or AccessDeniedException for @PreAuthorize)

    /// Happy Path, JumpExists and gets deleted
    @Test
    void deleteJump_whenExists_shouldDelete() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        Jump existingJump = new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                aircraft,
                dropzone,
                jumptype,
                user,
                "Delete Notes"
        );

        jumpRepository.save(existingJump);

        JumpLookupDTO deletedLookupDtoJump = jumpService.deleteJump(existingJump.getId());

        Optional<Jump> reloadedJump = jumpRepository.findById(existingJump.getId());

        // Assert
        assertTrue(reloadedJump.isEmpty());
        assertThrows(ResourceNotFoundException.class,
                () -> jumpService.deleteJump(existingJump.getId()));

        // Assert Deleted DTO

        assertEquals(existingJump.getId(), deletedLookupDtoJump.getId());
        assertEquals(1, deletedLookupDtoJump.getJumpNumber());
        assertEquals(existingJump.getAircraft().getAircraftName(), deletedLookupDtoJump.getAircraft().getAircraftName());
        assertTrue(deletedLookupDtoJump.getJumpNotes().contains("Delete"));
    }


    /// Test When Jump is Missing
    @Test
    void deleteJump_whenJumpMissing_shouldThrowResourceNotFoundException() {

        // Assert
        assertThrows(ResourceNotFoundException.class,
                () -> jumpService.deleteJump(Long.MAX_VALUE));
    }


// GET JUMP TESTS
    /// Jump Found
    @Test
    void getJump_whenExists_shouldReturnJumpLookupDTO() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        Jump existingJump = new Jump(
                null,
                UUID.randomUUID(),
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                aircraft,
                dropzone,
                jumptype,
                user,
                "Delete Notes"
        );

        jumpRepository.save(existingJump);

        JumpLookupDTO foundJump = jumpService.getJump(existingJump.getId());

        // Assert

        assertEquals(existingJump.getId(), foundJump.getId());
        assertEquals(existingJump.getDropzone().getDropzoneName(), foundJump.getDropzone().getDropzoneName());
        assertEquals(1, foundJump.getJumpNumber());
        assertEquals(existingJump.getUser().getId(), foundJump.getUser().getId());
        assertEquals(10000, foundJump.getAltitude());
    }

    /// Jump Not Found
    @Test
    void findByUuid_whenMissing_shouldReturnResourceNotFoundException() {
        // Assert
        assertThrows(ResourceNotFoundException.class,
                () -> jumpService.getJump(Long.MAX_VALUE));
    }


// GET ALL JUMPS TESTS
    // Get ALL jumps (Pagination Check)
    @Test
    void allJumps_CheckCorrectPagination() {
        // Set up
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());
        Pageable page0 = PageRequest.of(0, 10);
        Pageable page1 = PageRequest.of(1, 10);
        Pageable page2 = PageRequest.of(2, 10);

        // Create 25 Jumps
        List<Jump> allJumps = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            Jump jump = createValidJump();
            jump.setUser(user);
            jump.setDropzone(dropzone);
            jump.setAircraft(aircraft);
            jump.setJumptype(jumptype);
            jump.setJumpNotes("-" + i + "-" + "Test Notes");
            allJumps.add(jump);
        }

        jumpRepository.saveAll(allJumps);

        // Act - Call Pages
        Page<JumpLookupDTO> jumpPage0 = jumpService.getAllJumps(user.getId(), page0);
        Page<JumpLookupDTO> jumpPage1 = jumpService.getAllJumps(user.getId(), page1);        Page<JumpLookupDTO> jumpPage2 = jumpService.getAllJumps(user.getId(), page2);

        // Assert
        assertEquals(10, jumpPage0.getContent().size());
        assertEquals(10, jumpPage1.getContent().size());
        assertEquals(5, jumpPage2.getContent().size());
        assertEquals(25, jumpPage0.getTotalElements());
        assertEquals(3, jumpPage2.getTotalPages());
    }

// TEST TOTAL FREE FALL TIME
    @Test
    void totalFreeFall_checkCorrectSum() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        // Create 5 Jumps
        List<Jump> allJumps = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Jump jump = createValidJump();
            jump.setUser(user);
            jump.setDropzone(dropzone);
            jump.setAircraft(aircraft);
            jump.setJumptype(jumptype);
            jump.setJumpNotes("-" + i + "-" + "Test Notes");
            allJumps.add(jump);
        }

        jumpRepository.saveAll(allJumps);

        String duration = jumpService.getTotalFreefallTime(user.getId());

        // Debug Print Result
        System.out.println("\n-----");
        System.out.println(duration);
        System.out.println("-----\n");

        // Assert
        assertTrue(duration.contains("0h : 4m : 10s"));
    }

// TEST GET TOTAL JUMPS
    @Test
    void totalJumpsPerUser_checkCorrectSum() {
        User user = userRepository.save(createValidUser());
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());
        Jumptype jumptype = jumptypeRepository.save(createValidJumptype());

        // Create 22 Jumps
        List<Jump> allJumps = new ArrayList<>();

        for (int i = 0; i < 22; i++) {
            Jump jump = createValidJump();
            jump.setUser(user);
            jump.setDropzone(dropzone);
            jump.setAircraft(aircraft);
            jump.setJumptype(jumptype);
            jump.setJumpNotes("-" + i + "-" + "Test Notes");
            allJumps.add(jump);
        }

        jumpRepository.saveAll(allJumps);

        //Assert
        assertEquals(22, jumpService.getTotalNumberOfJumps(user.getId()));
    }


// TEST SEARCH BY CRITERIA
    /// Happy Path - Search Date Range Only
    @Test
    void searchJumps_dateSearchOnly() {

        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        LocalDateTime from = LocalDateTime.of(2025, 8, 24, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 8, 30, 0, 0);

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, from, to, null, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> !j.getJumpDate().isBefore(from) && !j.getJumpDate().isAfter(to))
                .count();

        assertEquals(expected, result.getTotalElements());
    }


    /// Happy Path - Search Jump Type Only
    @Test
    void searchJumps_jumptypeSearchOnly() {
        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        Jumptype belly = (Jumptype) seed.get(userId).get("belly");
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, null, null, belly, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> j.getJumptype().getId().equals(belly.getId()))
                .count();

        assertEquals(expected, result.getTotalElements());
    }


    /// Combined Filters - Date Range & Type
    @Test
    void searchJumps_combinedFilters() {
        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        Jumptype belly = (Jumptype) seed.get(userId).get("belly");
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        LocalDateTime from = LocalDateTime.of(2025, 8, 25, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 8, 30, 0, 0);

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, from, to, belly, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> j.getJumptype().getId().equals(belly.getId()))
                .filter(j -> !j.getJumpDate().isBefore(from) && !j.getJumpDate().isAfter(to))
                .count();

        assertEquals(expected, result.getTotalElements());
    }

    /// Open Ended Date Ranges (From null or To null)
    @Test
    void searchJumps_openEndedToDate() {
        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        LocalDateTime to = LocalDateTime.of(2025, 8, 28, 0, 0);

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, null, to, null, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> !j.getJumpDate().isAfter(to))
                .count();

        assertEquals(expected, result.getTotalElements());
    }

    @Test
    void searchJumps_openEndedFromDate() {
        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        LocalDateTime from = LocalDateTime.of(2025, 8, 28, 0, 0);

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, from, null, null, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> !j.getJumpDate().isBefore(from))
                .count();

        assertEquals(expected, result.getTotalElements());
    }

    /// Boundary conditions (Check Inclusivity on boundaries of Date Searches)
    @Test
    void searchJumps_dateSearchBottom() {

        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        LocalDateTime date = LocalDateTime.of(2025, 8, 24, 0, 0);

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, date, date, null, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> j.getJumpDate().toLocalDate().equals(date.toLocalDate()))
                .count();

        assertEquals(expected, result.getTotalElements());
    }

    @Test
    void searchJumps_dateSearchTop() {

        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        LocalDateTime date = LocalDateTime.of(2025, 8, 30, 0, 0);

        Page<JumpLookupDTO> result = jumpService.searchJumps(userId, date, date, null, PageRequest.of(0, 20));

        long expected = jumps.stream()
                .filter(j -> j.getJumpDate().toLocalDate().equals(date.toLocalDate()))
                .count();

        assertEquals(expected, result.getTotalElements());
    }


    /// Check Correct Pagination

    @Test
    void searchJumps_CheckCorrectPagination() {

        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long userId = seed.keySet().stream().findFirst().get();
        List<Jump> jumps = (List<Jump>) seed.get(userId).get("jumps");

        Pageable page0 = PageRequest.of(0, 3);
        Pageable page1 = PageRequest.of(1, 3);
        Pageable page3 = PageRequest.of(3, 3);

        Page<JumpLookupDTO> page0Result = jumpService.searchJumps(userId, null, null, null, page0);
        Page<JumpLookupDTO> page1Result = jumpService.searchJumps(userId, null, null, null, page1);
        Page<JumpLookupDTO> page3Result = jumpService.searchJumps(userId, null, null, null, page3);

        assertEquals(jumps.size(), page0Result.getTotalElements());
        assertEquals(3, page0Result.getContent().size());
        assertEquals(3, page1Result.getContent().size());
        assertEquals(2, page3Result.getContent().size());
        assertEquals((int) Math.ceil(jumps.size() / 3.0), page3Result.getTotalPages());
    }

    /// User Isolation (Ensure that User1 can not get User2 Jumps)
    @Test
    void searchJumps_userIsolation() {
        Map<Long, Map<String, Object>> seed = searchSpecificationSeedData();
        Long user1Id = seed.keySet().stream().findFirst().get();
        Long user2Id = seed.keySet().stream().skip(1).findFirst().get();
        List<Jump> user2Jumps = (List<Jump>) seed.get(user2Id).get("jumps");

        Page<JumpLookupDTO> result = jumpService.searchJumps(user1Id, null, null, null, PageRequest.of(0, 50));

        // Assert that none of user2 jumps appear
        for (JumpLookupDTO dto : result.getContent()) {
            assertNotEquals(user2Id, dto.getUser().getId());
        }
        // Optional: total elements <= user1 jumps
        List<Jump> user1Jumps = (List<Jump>) seed.get(user1Id).get("jumps");
        assertEquals(user1Jumps.size(), result.getTotalElements());
    }


// Helpers

    private Map<Long, Map<String, Object>> searchSpecificationSeedData() {
        // Create Users
        User user1 = createValidUser();
        user1.setUsername("username1@test.com");
        user1 = userRepository.save(user1);

        User user2 = createValidUser();
        user2.setUsername("username2@test.com");
        user2 = userRepository.save(user2);

        // Aircraft & Dropzone
        Aircraft aircraft = aircraftRepository.save(createValidAircraft());
        Dropzone dropzone = dropzoneRepository.save(createValidDropzone());

        // Jumptypes
        Jumptype belly = createValidJumptype();
        belly.setJumptypeName("Belly");
        belly = jumptypeRepository.save(belly);

        Jumptype angle = createValidJumptype();
        angle.setJumptypeName("Angle");
        angle = jumptypeRepository.save(angle);

        LocalDateTime baseDate = LocalDateTime.of(2025, 8, 30, 0, 0);

        List<Jump> user1Jumps = new ArrayList<>();
        List<Jump> user2Jumps = new ArrayList<>();

        // User1: 7 Belly
        for (int i = 0; i < 7; i++) {
            Jump jump = createValidJump();
            jump.setUser(user1); jump.setJumpDate(baseDate.minusDays(i));
            jump.setDropzone(dropzone); jump.setAircraft(aircraft); jump.setJumptype(belly);
            jump.setJumpNotes("Belly-" + i); user1Jumps.add(jump);
        }

        // User1: 4 Angle
        for (int i = 0; i < 4; i++) {
            Jump jump = createValidJump();
            jump.setUser(user1); jump.setJumpDate(baseDate.minusDays(i));
            jump.setDropzone(dropzone); jump.setAircraft(aircraft); jump.setJumptype(angle);
            jump.setJumpNotes("Angle-" + i); user1Jumps.add(jump);
        }

        jumpRepository.saveAll(user1Jumps);

        // User2: 4 Belly
        for (int i = 0; i < 4; i++) {
            Jump jump = createValidJump();
            jump.setUser(user2); jump.setJumpDate(baseDate.minusDays(i));
            jump.setDropzone(dropzone); jump.setAircraft(aircraft); jump.setJumptype(belly);
            jump.setJumpNotes("Belly2-" + i); user2Jumps.add(jump);
        }

        jumpRepository.saveAll(user2Jumps);

        Map<Long, Map<String, Object>> seedData = new HashMap<>();
        seedData.put(user1.getId(), Map.of(
                "user", user1,
                "belly", belly,
                "angle", angle,
                "jumps", user1Jumps));
        seedData.put(user2.getId(), Map.of(
                "user", user2,
                "belly", belly,
                "angle", angle,
                "jumps", user2Jumps));

        return seedData;
    }






    private Jump createValidJump() {
        Jump jump = new Jump();
        jump.setUser(null);
        jump.setAltitude(10000);
        jump.setFreeFallDuration(50);
        jump.setJumpDate(LocalDateTime.now().toLocalDate().atStartOfDay());
        jump.setJumpNotes("Test Jump Notes");
        jump.setDropzone(new Dropzone(null, "TestDropzone"));
        jump.setAircraft(new Aircraft(null, "TestAircraft"));
        jump.setJumptype(new Jumptype(null, "TestJumptype"));

        return jump;
    }

    private JumpInsertDTO createValidJumpInsertDTO() {
        return new JumpInsertDTO(
                10000,
                50,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                "Test Jump Notes", null,null,null,null
        );
    }

    private JumpUpdateDTO createValidJumpUpdateDTO() {
        return new JumpUpdateDTO(
                12000,
                60,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                "Update Test Jump Notes", null,null,null
        );
    }

    private User createValidUser() {
        return new User(
                null, UUID.randomUUID(),true,
                "username@test.com", "a@123456",
                "Firstname", "Lastname", Role.SKYDIVER
        );
    }

    private Dropzone createValidDropzone() {
        return new Dropzone(
                null, "Athens"
        );
    }


    private Jumptype createValidJumptype() {
        return new Jumptype(
                null, "Belly"
        );
    }


    private Aircraft createValidAircraft() {
        return new Aircraft(
                null, "Cessna"
        );
    }

}

