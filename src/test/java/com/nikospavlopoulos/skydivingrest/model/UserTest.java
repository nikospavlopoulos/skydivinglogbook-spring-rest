package com.nikospavlopoulos.skydivingrest.model;

import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserTest {

    @Autowired
    private UserRepository userRepository;

    // Test user persists in DB - Verify Successful Create
    @Test
    void userCreationTest() {
        User user = new User();
        user.setUsername("test@test.com");
        user.setPassword("12345");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");

        User savedUser = userRepository.saveAndFlush(user);

        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        // Asserts

        assertThat(foundUser.getId()).isNotNull();
        assertThat(foundUser.getUuid()).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("test@test.com");
        assertThat(foundUser.getPassword()).isNotNull();
        assertThat(foundUser.getFirstname()).isEqualTo("Firstname");
        assertThat(foundUser.getLastname()).isEqualTo("Lastname");
    }

    // Test not nullable values - Throw Exception DataIntegrityViolationException

    @Test
    void userValidationTest() {

        List<String> fields = List.of("username", "password", "firstname", "lastname");


        //Loop through fields checking and setting each for null
        for (String field : fields) {

            // Initiate the valid user for each loop
            User user = createValidUser();

            // Check the active field of the loop
            // Set it to null
            if (field.equals("username")) {
                user.setUsername(null);
            }
            else if (field.equals("password")) {
                user.setPassword(null);
            }
            else if (field.equals("firstname")) {
                user.setFirstname(null);
            }
            else if (field.equals("lastname")) {
                user.setLastname(null);
            }

            //Assert DataIntegrityViolation
            assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(user));
        }
    }


    // Test email validation - Username - Throws DataIntegrityViolation

    @Test
    void emailValidationTest() {
        User user = new User();
        user.setUsername("Test");

        // Assert Throw ConstraintViolationException
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }


    // Test duplicate username validation - Throws DataIntegrityViolation

    @Test
    void duplicateUsernameTest() {
        User user1 = createValidUser();
        userRepository.saveAndFlush(user1);

        User user2 = new User();
        user2.setUsername("test@test.com");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(user2));
    }

    // test UUID generation
    @Test
    void generateUUIDTest() {
        User user = createValidUser();
        userRepository.saveAndFlush(user);

        assertNotNull(user.getUuid());
    }
    @Test
    void generateUUIDTestNotNull() {
        User user = createValidUser();

        UUID notNullUuid = UUID.randomUUID();
        user.setUuid(notNullUuid);

        userRepository.saveAndFlush(user);

        assertEquals(notNullUuid, user.getUuid());
    }

    // Helper Function

    private User createValidUser(){
        User user = new User();
        user.setUsername("test@test.com");
        user.setPassword("12345");
        user.setFirstname("Firstname");
        user.setLastname("Lastname");
        return user;
    }


}