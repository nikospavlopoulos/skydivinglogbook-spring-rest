package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.exceptions.*;
import com.nikospavlopoulos.skydivingrest.dto.PasswordUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface handling User related Operations *
 * Operations: Create, Edit/Update , Delete, Get, List(used by ADMINS)
 */

public interface IUserService {

    // Create user
    UserLookupDTO createUser(UserInsertDTO dto) throws InvalidArgumentException, ResourceConflictException, ValidationException;

    // Update User
    UserLookupDTO updateUser(Long id, UserUpdateDTO dto) throws InvalidArgumentException,ValidationException, ResourceNotFoundException;


    // Soft-Delete User (soft delete with deactivating)
    UserLookupDTO deactivateUser(Long id);


    // Get User
    UserLookupDTO getUser(Long id) throws ResourceNotFoundException;


    // List Users
    Page<UserLookupDTO> getAllUsers(int page, int size);


    // change Password
    void changePassword(Long id, PasswordUpdateDTO dto) throws ResourceConflictException, ResourceNotFoundException, UnauthorizedException;


}
