package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.core.exceptions.*;
import com.nikospavlopoulos.skydivingrest.dto.PasswordUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.mapper.UserMapper;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

/**
 * Implements {@link IUserService} for managing users.
 * Handles creation, updates, soft-deletion, fetching, and password changes.
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLookupDTO createUser(UserInsertDTO dto) throws InvalidArgumentException, ResourceConflictException, ValidationException {

        Role DEFAULT_USER_ROLE = Role.SKYDIVER;

        // Normalize (trim & LowerCase) username/email
        String username = dto.getUsername().trim().toLowerCase(Locale.ROOT);

        // Debugging user already exists
        System.out.println("\n------");
        System.out.println("Normalized username: " + username);
        System.out.println("DB count: " + userRepository.count());
        System.out.println("Existing user: " + userRepository.findByUsernameAndActiveIsTrue(username));
        System.out.println("------\n");

        if (userRepository.findByUsernameAndActiveIsTrue(username).isPresent()) {
            throw new ResourceConflictException("The user with email: " + username + " already exists.", HttpStatus.CONFLICT);
        };

        User user = userMapper.userInsertDTOtoUser(dto);

        user.setRole(DEFAULT_USER_ROLE);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // In case there is DataIntegrityViolation during DB persist
        try {
            User savedUser = userRepository.save(user);
            return userMapper.userToUserLookupDTO(savedUser);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("Data Integrity - The User with email: " + username + " already exists.", HttpStatus.CONFLICT);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLookupDTO updateUser(Long id, UserUpdateDTO dto) throws InvalidArgumentException, ValidationException, ResourceNotFoundException {

        Optional<User> existingUser = userRepository.findByIdAndActiveIsTrue(id);

        if (existingUser.isEmpty()) {
            throw new ResourceNotFoundException("The user with id: " + id + " is not found.", HttpStatus.NOT_FOUND);
        };

        User user = existingUser.get();

        //Normalize username and update
        String updatedUsername = dto.getUsername() != null ? dto.getUsername().trim().toLowerCase() : user.getUsername();

        Optional<User> findUserDuplicate = userRepository.findByUsername(updatedUsername);

        if (!updatedUsername.equals(user.getUsername())) {
            if (findUserDuplicate.isPresent() && !findUserDuplicate.get().getId().equals(id) ) { // Checks if user already exists but is not the current user we are updating
                throw new ResourceConflictException("The User with email: " + updatedUsername + " already exists.", HttpStatus.CONFLICT);
            }
            user.setUsername(updatedUsername);
        }

        // Update Firstname
        String updatedFirstname = dto.getFirstname() != null ? dto.getFirstname() : user.getFirstname() ;

        if (!updatedFirstname.equals(user.getFirstname())) {
            user.setFirstname(updatedFirstname);
        }

        // Update Lastname
        String updatedLastname = dto.getLastname() != null ? dto.getLastname() : user.getLastname();

        if (!updatedLastname.equals(user.getLastname())) {
            user.setLastname(updatedLastname);
        }

        User updatedUser = userRepository.save(user);

        return userMapper.userToUserLookupDTO(updatedUser);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLookupDTO deactivateUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The Active user with id: " + id + " is not found.", HttpStatus.NOT_FOUND)
        );

        // Deactivate (active = false) only active users
        if (user.getActive().equals(Boolean.TRUE)) {
            user.setActive(false);
            User deactivatedUser = userRepository.save(user);
        }

        return userMapper.userToUserLookupDTO(user);
    }


    @Override
    @Transactional(readOnly = true)
    public UserLookupDTO getUser(Long id) throws ResourceNotFoundException {
        // Retrieve User
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("The user with id: " + id + " is not found.", HttpStatus.NOT_FOUND)
        );

        return userMapper.userToUserLookupDTO(user);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserLookupDTO> getAllUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<User> allUsers = userRepository.findAll(pageable);

        return userMapper.userListToUserLookupDTO(allUsers);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long id, PasswordUpdateDTO dto) throws ResourceConflictException, ResourceNotFoundException, UnauthorizedException {

        User user = userRepository.findByIdAndActiveIsTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("The user with id: " + id + " is not found.", HttpStatus.NOT_FOUND)
        );

        String oldPassword = dto.getOldPassword();


        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw  new UnauthorizedException("Old password does not match", HttpStatus.UNAUTHORIZED);
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ResourceConflictException("Password is the same", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }
}
