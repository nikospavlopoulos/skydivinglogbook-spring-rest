package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ResourceConflictException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.ResourceNotFoundException;
import com.nikospavlopoulos.skydivingrest.core.exceptions.UnauthorizedException;
import com.nikospavlopoulos.skydivingrest.dto.PasswordUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.mapper.UserMapper;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests for UserServiceImpl.
 * Covers user creation, updates, and exception handling.
 */


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

// Create User

    // Happy Path - Everything works as expected
    @Test
    void createUser_shouldReturnAllUserDetails() {

        // Debug User Already Exists
        System.out.println("\n------");
        System.out.println("Users in DB before test: " + userRepository.count());
        System.out.println("------\n");


        //Arrange
        UserInsertDTO insertDTO = new UserInsertDTO(
                " UserNameService@tesT.com",
                "a@123456",
                "Firstname",
                "Lastname"
        );

        UserLookupDTO savedUser = userService.createUser(insertDTO);

        // Asserts
        assertEquals("usernameservice@test.com", savedUser.getUsername());
        assertEquals("Firstname", savedUser.getFirstname());
        assertEquals("Lastname", savedUser.getLastname());
        assertTrue(passwordEncoder.matches("a@123456", userRepository.findById(savedUser.getId()).get().getPassword()));
    }



    // Username Exists - Check Throws Exception
    @Test
    void createUser_UsernameExists_throwsException() {
        //Arrange
        User existingUser = userRepository.save(createValidUser());


        UserInsertDTO insertDTO = new UserInsertDTO(
                " UserNameService@tesT.com",
                "a@123456",
                "Firstname",
                "Lastname"
        );

        //Asserts

        assertThrows(ResourceConflictException.class, () -> userService.createUser(insertDTO));

    }

// Update User
    // Test Happy path - Everything works as expected
    @Test
    void updateUser_shouldUpdateAllUserDetails() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                " NewuserNameservice@test.com",
                "newFirstname",
                "newLastname"
        );

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        // Asserts
        assertEquals("newusernameservice@test.com", updatedUser.getUsername());
        assertEquals("newFirstname", updatedUser.getFirstname());
        assertEquals("newLastname", updatedUser.getLastname());

        User updatedUserEntity = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("newusernameservice@test.com", updatedUserEntity.getUsername());
        assertEquals("newFirstname", updatedUserEntity.getFirstname());
        assertEquals("newLastname", updatedUserEntity.getLastname());
    }

    // Test User Not Found - Check throws Exception
    @Test
    void updateUser_UsernameNotFound_throwsException() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                " NewuserNameservice@test.com",
                "newFirstname",
                "newLastname"
        );

        //Asserts

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(Long.MAX_VALUE, updateDTO));
    }

    // Test Update - Same Firstname
    @Test
    void updateUser_UsernameExists_SameFirstname() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                " NewuserNameservice@test.com",
                "Firstname",
                "newLastname"
        );

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        // Asserts
        assertEquals("newusernameservice@test.com", updatedUser.getUsername());
        assertEquals("Firstname", updatedUser.getFirstname());
        assertEquals("newLastname", updatedUser.getLastname());

        User updatedUserEntity = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("newusernameservice@test.com", updatedUserEntity.getUsername());
        assertEquals("Firstname", updatedUserEntity.getFirstname());
        assertEquals("newLastname", updatedUserEntity.getLastname());
    }

    // Test Update - Same Lastame
    @Test
    void updateUser_UsernameExists_SameLastname() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                " NewuserNameservice@test.com",
                "newFirstname",
                "Lastname"
        );

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        // Asserts
        assertEquals("newusernameservice@test.com", updatedUser.getUsername());
        assertEquals("newFirstname", updatedUser.getFirstname());
        assertEquals("Lastname", updatedUser.getLastname());

        User updatedUserEntity = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("newusernameservice@test.com", updatedUserEntity.getUsername());
        assertEquals("newFirstname", updatedUserEntity.getFirstname());
        assertEquals("Lastname", updatedUserEntity.getLastname());
    }

    // Test Update - Same Username
    @Test
    void updateUser_UsernameExists_SameUsername() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                " UserNameservice@test.com",
                "newFirstname",
                "newLastname"
        );

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        // Asserts
        assertEquals("usernameservice@test.com", updatedUser.getUsername());
        assertEquals("newFirstname", updatedUser.getFirstname());
        assertEquals("newLastname", updatedUser.getLastname());

        User updatedUserEntity = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("usernameservice@test.com", updatedUserEntity.getUsername());
        assertEquals("newFirstname", updatedUserEntity.getFirstname());
        assertEquals("newLastname", updatedUserEntity.getLastname());
    }

    // Test - Username Already Exists with Update Username
    @Test
    void updatedUser_UpdatedUsernameMatchesDifferentUser_ThrowsResourceConflictException() {
        User existingUser = new User(
                null,
                UUID.randomUUID(),
                true,
                "existing@test.com",
                "a@456789",
                "existingFirstname",
                "existingLastname",
                Role.SKYDIVER
        );

        userRepository.save(existingUser);

        User updatingUser = new User(
                null,
                UUID.randomUUID(),
                true,
                "updating@test.com",
                "a@456789",
                "updatingFirstname",
                "updatingLastname",
                Role.SKYDIVER
        );

        userRepository.save(updatingUser);

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "existing@test.com",
                "updatingFirstname",
                "updatingLastname"
        );


        //Asserts

        assertThrows(ResourceConflictException.class, () -> userService.updateUser(updatingUser.getId(), updateDTO));

        assertTrue((assertThrows(ResourceConflictException.class, () -> userService.updateUser(updatingUser.getId(), updateDTO))).getMessage().contains("already exists"));
    }

    @Test
    void updateUser_UsernameSameAsCurrent_DoesNotThrowException() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "usernameservice@test.com", // same username
                "newFirstname",
                "newLastname"
        );

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        // Assert
        assertEquals("usernameservice@test.com", updatedUser.getUsername());
        assertEquals("newFirstname", updatedUser.getFirstname());
        assertEquals("newLastname", updatedUser.getLastname());
    }


    // Covering Cases where Null fields are in the DTO
    @Test
    void updateUser_WithNullFields_DoesNotChangeOriginal() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                null,
                null,
                null
        );

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        assertEquals(existingUser.getUsername(), updatedUser.getUsername());
        assertEquals(existingUser.getFirstname(), updatedUser.getFirstname());
        assertEquals(existingUser.getLastname(), updatedUser.getLastname());
    }

    // Username Normalization (Trim & Lowercase
    @Test
    void updateUser_NormalizesUsername_TrimAndLowercase() {
        User existingUser = userRepository.save(createValidUser());

        UserUpdateDTO updateDTO = new UserUpdateDTO("  USERnaMEserVice@TEST.com  ", "newFirstname", "newLastname");

        UserLookupDTO updatedUser = userService.updateUser(existingUser.getId(), updateDTO);

        assertEquals("usernameservice@test.com", updatedUser.getUsername());
    }


// Deactivate User

    // Test UserFound - Deactivate Persists
    @Test
    void deactivateUser_Found_DeactivateSuccesful() {
        User activeUser = userRepository.save(createValidUser());

        UserLookupDTO deactivateUser = userService.deactivateUser(activeUser.getId());

        // Assert

        assertEquals(false, deactivateUser.getActive());
    }

    // Test UserNotFound - ResourceNotFoundException
    @Test
    void deactivateUser_NotFound_ThrowsException() {
        userRepository.save(createValidUser());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deactivateUser(Long.MAX_VALUE));
    }


    // Test Inactive UserFound - returns same user
    @Test
    void deactivateUser_FoundInactive_ReturnUser() {
        User inactiveUser = userRepository.save(createValidUser());
        inactiveUser.setActive(false);
        inactiveUser = userRepository.save(inactiveUser);

        UserLookupDTO deactivateUser = userService.deactivateUser(inactiveUser.getId());

        // Assert

        assertEquals(false, deactivateUser.getActive());
        assertEquals("usernameservice@test.com", deactivateUser.getUsername());
        assertEquals("Firstname", deactivateUser.getFirstname());
        assertEquals("Lastname", deactivateUser.getLastname());
        assertEquals(Role.SKYDIVER, deactivateUser.getRole());
    }





// Get user

    // Test User Found - returns all Details
    @Test
    void getUser_Found_ReturnsAllDetails() {
        User user = userRepository.save(createValidUser());
        
        UserLookupDTO foundUser = userService.getUser(user.getId());
        
        // Assert
        assertEquals(user.getUuid() ,foundUser.getUuid());
        assertEquals(user.getId() ,foundUser.getId());
        assertEquals(true, foundUser.getActive());
        assertEquals("usernameservice@test.com", foundUser.getUsername());
        assertEquals("Firstname", foundUser.getFirstname());
        assertEquals("Lastname", foundUser.getLastname());
        assertEquals(Role.SKYDIVER, foundUser.getRole());
    }
    

    // Test user not found - Throws exception
    @Test
    void getUser_NotFound_ThrowsException() {
        User user = userRepository.save(createValidUser());

       // Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(Long.MAX_VALUE));
    }

// Get ALL users (Pagination Check)
    @Test
    void allUsers_CheckCorrectPagination() {
        List<User> allUsers = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            User user = createValidUser();
            user.setUsername("usernameservice" + i + "@test.com");
            allUsers.add(user);
        }

        userRepository.saveAll(allUsers);

        // Act - call pages
        Page<UserLookupDTO> page0 = userService.getAllUsers(0, 10);
        Page<UserLookupDTO> page1 = userService.getAllUsers(1, 10);
        Page<UserLookupDTO> page2 = userService.getAllUsers(2, 10);

        // Assert
        assertEquals(10, page0.getContent().size());
        assertEquals(10, page1.getContent().size());
        assertEquals(5, page2.getContent().size());
        assertEquals(25, page0.getTotalElements());
        assertEquals(3, page2.getTotalPages());
    }


// Change Password

    // Happy Path - Successfully Change Password
    @Test
    void changePassword_SuccessfulChange() {
        User user = userRepository.save(createValidUser());
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.save(user);

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO(
                "a@123456",
                "a@456789"
        );

        userService.changePassword(user.getId(), passwordUpdateDTO);

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

                //Assert
        assertTrue(passwordEncoder.matches(passwordUpdateDTO.getNewPassword(), updatedUser.getPassword()));
    }


    //  User Not Found - ResourceNotFoundException
    @Test
    void changePassword_UserNotFound_throwsNotFoundException() {
        User user = userRepository.save(createValidUser());

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO(
                "a@123456",
                "a@123456"
        );
        //Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.changePassword(Long.MAX_VALUE, passwordUpdateDTO));
    }


    // Wrong Old password - UnauthorizedException
    @Test
    void changePassword_WrongOldPassword_Unauthorized() {
        User user = userRepository.save(createValidUser());
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.save(user);

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO(
                "a@113466",
                "a@456789"
        );

        //Assert
        assertThrows(UnauthorizedException.class, () -> userService.changePassword(user.getId(), passwordUpdateDTO));
    }


    // Same password - ResourceConflict
    @Test
    void changePassword_SamePassword_ResourceConflict() {
        User user = userRepository.save(createValidUser());
        user.setPassword(passwordEncoder.encode("a@123456"));
        userRepository.save(user);

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO(
                "a@123456",
                "a@123456"
        );

        //Assert
        assertThrows(ResourceConflictException.class, () -> userService.changePassword(user.getId(), passwordUpdateDTO));

    }

    //Helpers

    private User createValidUser(){
        User user = new User();
        user.setUsername("usernameservice@test.com");
        user.setPassword("a@123456");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");
        user.setRole(Role.SKYDIVER);
        return user;
    }

}