package com.nikospavlopoulos.skydivingrest.rest;

import com.nikospavlopoulos.skydivingrest.dto.PasswordUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.UserUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.UserLookupDTO;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetails;
import com.nikospavlopoulos.skydivingrest.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    // POST api/users - User Registration
    @PostMapping
    public ResponseEntity<UserLookupDTO> registerUser(@Valid @RequestBody UserInsertDTO userInsertDTO) {

        UserLookupDTO userLookupDTO = userService.createUser(userInsertDTO);

        // create new URI - /api/users/{userId}
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userLookupDTO.getId())
                .toUri();

        /* (Alternative - simplistic)
        URI location = URI.create("/api/users/" + userLookupDTO.getId());
        */

        return ResponseEntity.created(location).body(userLookupDTO);

        // TODO: log message
    }

    // GET /users/{id} - Retrieve User Profile
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserLookupDTO> getUser(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails principal) {

        // Confirming Owner
        if (!id.equals(principal.getId())) {
            throw new AccessDeniedException("You can not access another user's profile");
        }

        UserLookupDTO userLookupDTO = userService.getUser(id);

        return ResponseEntity.ok(userLookupDTO);

        // TODO: log message

    }


    // PUT /users/{id} - Update User Info
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserLookupDTO> updateUser(@PathVariable("id") Long id, @RequestBody @Valid UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal CustomUserDetails principal) {

        // Confirming Owner
        if (!id.equals(principal.getId())) {
            throw new AccessDeniedException("You can not update another user's profile");
        }

        UserLookupDTO userLookupDTO = userService.updateUser(id, userUpdateDTO);

        return ResponseEntity.ok(userLookupDTO);

        // TODO: log message

    }


    // PUT /users/{id}/password - Change Password
    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<PasswordUpdateDTO.MessageResponse> changePassword(@PathVariable("id") Long id, @RequestBody @Valid PasswordUpdateDTO passwordUpdateDTO, @AuthenticationPrincipal CustomUserDetails principal) {

        // Confirming Owner
        if (!id.equals(principal.getId())) {
            throw new AccessDeniedException("You can not change another user's password");
        }

        userService.changePassword(id, passwordUpdateDTO);

        return ResponseEntity.ok(new PasswordUpdateDTO.MessageResponse("Password Updated Successfully"));

        // TODO: log message

    }

    // DELETE /users/{id} - Deactivate User (Soft Delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserLookupDTO> deactivateUser(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails principal) {

        // Confirming Owner
        if (!id.equals(principal.getId())) {
            throw new AccessDeniedException("You can not delete another user's profile");
        }

        UserLookupDTO userLookupDTO = userService.deactivateUser(id);

        return ResponseEntity.ok(userLookupDTO);

        // TODO: log message

    }

    // GET /users - List All Users (Admin Only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserLookupDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Page<UserLookupDTO> userLookupDTOPage = userService.getAllUsers(page, size);

        return ResponseEntity.ok(userLookupDTOPage);

        // TODO: log message

    }


}
