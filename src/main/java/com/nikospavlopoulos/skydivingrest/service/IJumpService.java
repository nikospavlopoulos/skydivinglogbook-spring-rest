package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.exceptions.GenericException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.InvalidArgumentException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ResourceNotFoundException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ValidationException;
import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.JumpUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import org.mapstruct.control.MappingControl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing skydiving jump records.
 * Handles CRUD operations, statistics calculations, and jump searching.
 * All operations are performed in the context of the authenticated user.
 *
 * Key responsibilities:
 * - Create, read, update, and delete jump records
 * - Calculate jump statistics (total jumps, total freefall time)
 * - Search jumps by date range and jump type
 * - Automatically calculate jump ordinal numbers
 *
 * @see JumpServiceImpl
 * @see com.nikospavlopoulos.skydivingrest.rest.JumpController
 */


public interface IJumpService {

    // Create Jump
    JumpLookupDTO createJump(JumpInsertDTO dto);

    // Update Jump
    JumpLookupDTO updateJump(Long id, JumpUpdateDTO dto) throws InvalidArgumentException, ResourceNotFoundException, ValidationException;

    // Hard Delete Jump
    JumpLookupDTO deleteJump(Long id) throws ResourceNotFoundException;

    // Get Jump
    JumpLookupDTO getJump(Long id) throws ResourceNotFoundException;

    // Get All Jumps
    Page<JumpLookupDTO> getAllJumps(Long id, Pageable pageable);

    // Get Total Freefall Time (sumTotalFreeFallDurationByUser)
    String getTotalFreefallTime(Long userId);

    // Get Total Number of Jumps (countByUserId)
    long getTotalNumberOfJumps(Long userId);

    // Search Jumps According to Date - Jumptype
    Page<JumpLookupDTO> searchJumps(Long userId, LocalDateTime jumpDateFrom, LocalDateTime jumpDateTo, Jumptype jumptype, Pageable pageable) throws ResourceNotFoundException;

}