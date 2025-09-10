package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.exceptions.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the IJumpService interface, providing business logic for managing skydiving jump records.
 * Handles CRUD operations for Jump entities, including validation, mapping, and exception handling.
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
            Jump savedJump = jumpRepository.save(jump);
            return jumpMapper.jumpToJumpLookupDTO(savedJump);
        } catch (DataIntegrityViolationException ex) {
            throw new InternalServerException("Data Integrity - The Jump failed to save.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @Override
    @Transactional
    public JumpLookupDTO deleteJump(Long id) throws ResourceNotFoundException {


        Jump jump = jumpRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The Requested Jump with id: " + id + " is not found", HttpStatus.NOT_FOUND));

        long jumpNumber = jumpRepository.countByUserIdAndIdLessThanEqual(jump.getUser().getId(), jump.getId());

        JumpLookupDTO jumpDetails = jumpMapper.jumpToJumpLookupDTO(jump);
        jumpDetails.setJumpNumber(jumpNumber);

        jumpRepository.delete(jump);

        return jumpDetails;
    }

    @Override
    @Transactional(readOnly = true)
    public JumpLookupDTO getJump(Long id) throws ResourceNotFoundException {

        Jump jump = jumpRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The Requested Jump with id: " + id + " is not found", HttpStatus.NOT_FOUND));

        long jumpNumber = jumpRepository.countByUserIdAndIdLessThanEqual(jump.getUser().getId(), jump.getId());

        JumpLookupDTO foundJump = jumpMapper.jumpToJumpLookupDTO(jump);
        foundJump.setJumpNumber(jumpNumber);

        return foundJump;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JumpLookupDTO> getAllJumps(Long id, Pageable pageable) {

        Page<Jump> allJumps = jumpRepository.findAllByUserId(id, pageable);

        return jumpMapper.jumpListToJumpLookupDTO(allJumps);
    }

    @Override
    @Transactional
    public String getTotalFreefallTime(Long userId) {

        long totalFreeFall = jumpRepository.sumTotalFreeFallDurationByUser(userId);

        Duration totalFreeFallDuration = Duration.ofSeconds(totalFreeFall);

        long hours = totalFreeFallDuration.toHours() % 60;
        long minutes = totalFreeFallDuration.toMinutes() % 60;
        long seconds = totalFreeFallDuration.toSeconds() %60;

        return String.format("%dh : %dm : %ds", hours, minutes, seconds);
    }

    @Override
    @Transactional
    public long getTotalNumberOfJumps(Long userId) {

        return jumpRepository.countJumpByUserId(userId);

    }

    @Override
    @Transactional
    public Page<JumpLookupDTO> searchJumps(Long userId, LocalDateTime jumpDateFrom, LocalDateTime jumpDateTo, Jumptype jumptype, Pageable pageable) {

        if (jumpDateFrom.isAfter(jumpDateTo)) {
            throw new InvalidArgumentException("The 'From' date should be before the 'To' date", HttpStatus.BAD_REQUEST);
        }

        Specification<Jump> spec = JumpSpecifications.filterJumps(userId, jumpDateFrom, jumpDateTo, jumptype);

        Page<Jump> retrievedJumps = jumpRepository.findAll(spec, pageable);

        return jumpMapper.jumpListToJumpLookupDTO(retrievedJumps);
    }
}
