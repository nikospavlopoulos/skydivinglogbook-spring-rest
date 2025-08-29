package com.nikospavlopoulos.skydivingrest.mapper;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    // Test - Happy path | All fields populated correctly
    @Test
    void userMapperSuccess() {
        User user = createValidUser();

        UserLookupDTO dto = userMapper.userToUserLookupDTO(user);

        assertEquals(1L, dto.getId());
        assertEquals("test@test.com", dto.getUsername());
        assertEquals(user.getUuid(), dto.getUuid());
//        assertEquals(user.getPassword(), dto.getPassword());
        assertEquals("Nikos", dto.getFirstname());
        assertEquals("Test", dto.getLastname());
        assertEquals(Role.SKYDIVER, dto.getRole());
    }

    // Test - Whole Entity is Null

    @Test
    void userEntityNullFail() {
        UserLookupDTO dto = userMapper.userToUserLookupDTO(null);

        assertNull(dto);
    }


    // Test - Field in Entity is Null
    @ParameterizedTest
    @ValueSource(strings = {"id", "uuid", "username", "password", "firstname", "lastname", "role"})
    void userFieldNullFail(String field) {

        User user = createValidUser();


        switch (field) {
            case "id":{
                user.setId(null);
                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
                assertNull(dto.getId());
                break;}
            case "uuid":{
                user.setUuid(null);
                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
                assertNull(dto.getUuid());
                break;}
            case "username":{
                user.setUsername(null);
                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
                assertNull(dto.getUsername());
                break;}
//            case "password":{
//                user.setPassword(null);
//                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
//                assertNull(dto.getPassword());
//                break;}
            case "firstname":{
                user.setFirstname(null);
                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
                assertNull(dto.getFirstname());
                break;}
            case "lastname":{
                user.setLastname(null);
                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
                assertNull(dto.getLastname());
                break;}
            case "role":{
                user.setRole(null);
                UserLookupDTO dto = userMapper.userToUserLookupDTO(user);
                assertNull(dto.getRole());
                break;}
        }
    }


    // Helper functions

    private User createValidUser() {
        return new User(
                1L,
                UUID.randomUUID(),
                "test@test.com",
                "a@123456",
                "Nikos",
                "Test",
                Role.SKYDIVER);
    }

    private User createNullEntityUser() {
        return new User();
    }


}