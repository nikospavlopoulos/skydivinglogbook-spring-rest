package com.nikospavlopoulos.skydivingrest.service;


import com.nikospavlopoulos.skydivingrest.core.exceptions.ResourceConflictException;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.mapper.UserMapper;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests for UserServiceImpl.
 * Covering a case that needs pure Unit Testing
 * with mocking for DataIntegrity violation check
 */

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService; // real service, mocks injected

    @Test
    void createUser_DataIntegrity_throwsException() {
        // Arrange
        UserInsertDTO insertDTO = new UserInsertDTO(
                "usernameservice@test.com",
                "a@123456",
                "Firstname",
                "Lastname"
        );

        // Create a User object that the mapper will return
        User mockUser = new User();
        mockUser.setUsername(insertDTO.getUsername());
        mockUser.setPassword(insertDTO.getPassword());

        // Mock the mapper to return the mockUser
        when(userMapper.userInsertDTOtoUser(any(UserInsertDTO.class)))
                .thenReturn(mockUser);

        // Mock repository to simulate no existing user
        when(userRepository.findByUsernameAndActiveIsTrue(anyString()))
                .thenReturn(Optional.empty());

        // Mock repository save to throw DataIntegrityViolationException
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("DB constraint"));

        // Act + Assert
        ResourceConflictException ex = assertThrows(ResourceConflictException.class,
                () -> userService.createUser(insertDTO));

        assertTrue(ex.getMessage().contains("Data Integrity"));
    }

}
