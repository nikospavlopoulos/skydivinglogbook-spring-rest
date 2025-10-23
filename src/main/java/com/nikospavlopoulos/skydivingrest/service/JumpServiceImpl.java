package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.exceptions.InternalServerException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ResourceNotFoundException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ValidationException;
import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.JumpUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.mapper.*;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.*;
import com.nikospavlopoulos.skydivingrest.specification.JumpSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Implementation of the IJumpService interface {@link IJumpService},
 * providing business logic for managing skydiving jump records.
 * Handles CRUD operations, statistics calculations, and jump searching.
 * All operations are performed in the context of the authenticated user.
 */


@Service
@RequiredArgsConstructor
public class JumpServiceImpl implements IJumpService{

    private final JumpRepository jumpRepository;
    private final UserRepository userRepository;
    private final DropzoneRepository dropzoneRepository;
    private final AircraftRepository aircraftRepository;
    private final JumptypeRepository jumptypeRepository;

    private final JumpMapper jumpMapper;
    private final UserMapper userMapper;
    private final DropzoneMapper dropzoneMapper;
    private final AircraftMapper aircraftMapper;
    private final JumptypeMapper jumptypeMapper;

    /**
     * Creates a new jump record for the specified user.
     * Validates that all referenced entities (aircraft, dropzone, jumptype) exist.
     *
     * @param dto the jump data including altitude, date, and references to static data
     * @return the created jump with generated ID and calculated jump number
     * @throws ResourceNotFoundException if aircraft, dropzone, or jumptype not found
     * @throws ValidationException if jump data fails validation constraints
     * @throws InternalServerException if database persistence fails
     */
    @Override
    @Transactional
    public JumpLookupDTO createJump(JumpInsertDTO dto) {

        Jump jump = jumpMapper.jumpInsertDTOtoJumpEntity(dto);

        User user = userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("The User: " + dto.getUserId() + " is not found", HttpStatus.NOT_FOUND)
        );
        jump.setUser(user);

        Dropzone dropzone = dropzoneRepository.findById(dto.getDropzoneId()).orElseThrow(
                () -> new ResourceNotFoundException("The Dropzone: " + dto.getDropzoneId() + " is not found", HttpStatus.NOT_FOUND)
        );
        jump.setDropzone(dropzone);

        Aircraft aircraft = aircraftRepository.findById(dto.getAircraftId()).orElseThrow(
                () -> new ResourceNotFoundException("The Aircraft: " + dto.getAircraftId() + " is not found", HttpStatus.NOT_FOUND)
        );
        jump.setAircraft(aircraft);

        Jumptype jumptype = jumptypeRepository.findById(dto.getJumptypeId()).orElseThrow(
                () -> new ResourceNotFoundException("The Jumptype: " + dto.getJumptypeId() + " is not found", HttpStatus.NOT_FOUND)
        );
        jump.setJumptype(jumptype);

        // In case there is a failure during DB persist
        try {
            System.out.println("DEBUG Jump before save: " + jump);
            Jump savedJump = jumpRepository.save(jump);
            return jumpMapper.jumpToJumpLookupDTO(savedJump);
        } catch (DataIntegrityViolationException ex) {
            throw new InternalServerException("Data Integrity - The Jump failed to save.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing jump record with new data.
     * Validates that all referenced entities exist before updating.
     *
     * @param id the ID of the jump to update
     * @param dto the updated jump data
     * @return the updated jump with recalculated jump number
     * @throws ResourceNotFoundException if jump, aircraft, dropzone, or jumptype not found
     * @throws ValidationException if updated data fails validation constraints
     */
    @Override
    @Transactional
    public JumpLookupDTO updateJump(Long id, JumpUpdateDTO dto) throws ResourceNotFoundException {

        Jump jump = jumpRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The Requested Jump with id: " + id + " is not found", HttpStatus.NOT_FOUND));

        jump.setAltitude(dto.getAltitude());
        jump.setFreeFallDuration(dto.getFreeFallDuration());
        jump.setJumpDate(dto.getJumpDate().toLocalDate().atStartOfDay());
        jump.setJumpNotes(dto.getJumpNotes());
        jump.setAircraft(aircraftRepository.findById(dto.getAircraftId()).orElseThrow(
                () -> new ResourceNotFoundException("The Aircraft: " + dto.getAircraftId() + " is not found", HttpStatus.NOT_FOUND)));
        jump.setDropzone(dropzoneRepository.findById(dto.getDropzoneId()).orElseThrow(
                () -> new ResourceNotFoundException("The Dropzone: " + dto.getDropzoneId() + " is not found", HttpStatus.NOT_FOUND)));
        jump.setJumptype(jumptypeRepository.findById(dto.getJumptypeId()).orElseThrow(
                () -> new ResourceNotFoundException("The Jumptype: " + dto.getJumptypeId() + " is not found", HttpStatus.NOT_FOUND)));

        Jump updatedJump = jumpRepository.save(jump);

        return jumpMapper.jumpToJumpLookupDTO(updatedJump);
    }

    /**
     * Deletes a jump record permanently from the database (hard delete).
     * Retrieves the jump details including its ordinal number before deletion
     * to return complete information to the caller.
     *
     * @param id the ID of the jump to delete
     * @return the details of the deleted jump including its jump number
     * @throws ResourceNotFoundException if jump with the given ID does not exist
     */
    @Override
    @Transactional
    public JumpLookupDTO deleteJump(Long id) throws ResourceNotFoundException {


        Jump jump = jumpRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The Requested Jump with id: " + id + " is not found", HttpStatus.NOT_FOUND));

        long jumpNumber = jumpRepository.countByUserIdAndJumpDateLessThanEqualAndIdLessThanEqual(jump.getUser().getId(), jump.getJumpDate(), jump.getId());

        JumpLookupDTO jumpDetails = jumpMapper.jumpToJumpLookupDTO(jump);
        jumpDetails.setJumpNumber(jumpNumber);

        jumpRepository.delete(jump);

        return jumpDetails;
    }

    /**
     * Retrieves a single jump by ID with its calculated ordinal number.
     * The jump number represents this jump's position in the user's chronological
     * sequence of all jumps.
     *
     * @param id the ID of the jump to retrieve
     * @return the jump details including calculated jump number
     * @throws ResourceNotFoundException if jump with the given ID does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public JumpLookupDTO getJump(Long id) throws ResourceNotFoundException {

        Jump jump = jumpRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The Requested Jump with id: " + id + " is not found", HttpStatus.NOT_FOUND));

        long jumpNumber = jumpRepository.countByUserIdAndJumpDateLessThanEqualAndIdLessThanEqual(jump.getUser().getId(), jump.getJumpDate(), jump.getId());

        JumpLookupDTO foundJump = jumpMapper.jumpToJumpLookupDTO(jump);
        foundJump.setJumpNumber(jumpNumber);

        return foundJump;
    }

    /**
     * Retrieves all jumps for a specific user with pagination.
     * Each jump includes its calculated ordinal number based on chronological order
     * (by date, then by ID for same-day jumps).
     *
     * Results are returned as a page with metadata for frontend pagination controls.
     *
     * @param id the ID of the user whose jumps to retrieve
     * @param pageable pagination parameters (page number, size, sorting)
     * @return a page of jumps with calculated jump numbers
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JumpLookupDTO> getAllJumps(Long id, Pageable pageable) {

        Page<Jump> allJumps = jumpRepository.findAllByUserId(id, pageable);

        // Calculate Skydiver's jump's ordinal number (based on date and then on jump id - correct)
        return allJumps.map(jump -> {
            JumpLookupDTO jumpLookupDTO = jumpMapper.jumpToJumpLookupDTO(jump);
            long jumpNumber = jumpRepository.countByUserIdAndJumpDateLessThanEqualAndIdLessThanEqual(
                    jump.getUser().getId(),
                    jump.getJumpDate(),
                    jump.getId()
            );
            jumpLookupDTO.setJumpNumber(jumpNumber);
            return jumpLookupDTO;
        });
    }

    /**
     * Calculates the total freefall time across all of a user's jumps.
     * Returns the result formatted as "Xh : Ym : Zs" for display purposes.
     *
     * @param userId the ID of the user
     * @return formatted string representing total freefall time (e.g., "5h : 23m : 45s")
     */
    @Override
    @Transactional
    public String getTotalFreefallTime(Long userId) {

        long totalFreeFall = jumpRepository.sumTotalFreeFallDurationByUser(userId);

        Duration totalFreeFallDuration = Duration.ofSeconds(totalFreeFall);

        long hours = totalFreeFallDuration.toHours() % 60;
        long minutes = totalFreeFallDuration.toMinutes() % 60;
        long seconds = totalFreeFallDuration.toSeconds() % 60;

        return String.format("%dh : %dm : %ds", hours, minutes, seconds);
    }

    /**
     * Calculates the total number of jumps for a specific user.
     * Simple count of all jump records associated with the user.
     *
     * @param userId the ID of the user
     * @return the total count of jumps (long)
     */
    @Override
    @Transactional
    public long getTotalNumberOfJumps(Long userId) {

        return jumpRepository.countJumpByUserId(userId);

    }

    /**
     * Searches for jumps matching the specified criteria.
     * All parameters except userId are optional. Null values are ignored in the search.
     * Results are paginated and each jump includes its ordinal number.
     *
     * @param userId the ID of the user whose jumps to search (required)
     * @param jumpDateFrom start of date range (inclusive), null for no lower bound
     * @param jumpDateTo end of date range (inclusive), null for no upper bound
     * @param jumptype filter by jump type, null to include all types
     * @param pageable pagination parameters (page number, size, sorting)
     * @return page of jumps matching the criteria
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Transactional
    public Page<JumpLookupDTO> searchJumps(Long userId, LocalDateTime jumpDateFrom, LocalDateTime jumpDateTo, Jumptype jumptype, Pageable pageable) {


        Specification<Jump> spec = JumpSpecifications.filterJumps(userId, jumpDateFrom, jumpDateTo, jumptype);

        Page<Jump> retrievedJumps = jumpRepository.findAll(spec, pageable);

        return jumpMapper.jumpListToJumpLookupDTO(retrievedJumps);
    }
}
