package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.exceptions.*;
import com.nikospavlopoulos.skydivingrest.dto.PasswordUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing user accounts and profiles.
 * Handles user registration, user retrieval, profile updates, password management, and soft deletion.
 *
 * Key responsibilities:
 * - User registration with default SKYDIVER role
 * - Profile information updates
 * - Password change with verification
 * - User deactivation (soft delete)
 * - User retrieval for profile viewing
 *
 * Note: User deletion is implemented as soft delete (setting active=false)
 * to maintain referential integrity with jump records.
 *
 * @see UserServiceImpl
 * @see com.nikospavlopoulos.skydivingrest.rest.UserController
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
