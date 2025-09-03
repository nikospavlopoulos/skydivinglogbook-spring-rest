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
 * Interface handling Jump related Operations *
 * Operations: Create, Edit/Update , Delete, Get, List
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

// WHAT I WANT THE JUMP SERVICE TO DO:

/// When user inserts a jump through the UI form, a jump should be persisted as Jump Entity in the database

/// When the jump is returned after creation or after search it should also include the ordinal number demonstrating which jump number this is for the user. Service should calculate the jump ordinal number.

/// The user should be able to search for jumps according to these criteria: jumpDate (actually jumpdate range between start date and end date), jumpType. And/or a combination of these two. The result should be a paginated list with all the jumps fulfilling the search criteria. Each jump should also include the particular jump's ordinal number according to the User's total number of jumps and not the search result rows .

/// The Service should be able to calculate and return the User's total number of Jumps. This result is going to be used and should be visible in a home page in the UI

/// The Service should be able to calculate and return the User's total freefall time. Since the result is going to be in seconds this should be converted and expressed an hour.minute.second format. This is going to be used and should be visible in a home page in the UI.

/// The user should be able to edit a jump's details. The jump will be selected from a list that they searched, or a getAllJumps type list, and choose an edit button to get a form to submit the updated info. JumpUpdateDTO is going to be used as the vehicle to update jump's info

/// The user should be able to delete a jump. The jump will be selected from a list that they searched, or a getAllJumps type list, and choose a delete button to hard delete it (no soft deletes for jumps).