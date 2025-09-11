package com.nikospavlopoulos.skydivingrest.rest;

import com.nikospavlopoulos.skydivingrest.dto.JumpInsertDTO;
import com.nikospavlopoulos.skydivingrest.dto.JumpUpdateDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumpLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetails;
import com.nikospavlopoulos.skydivingrest.service.IJumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/jumps")
public class JumpController {

    private final IJumpService jumpService;

    // POST /api/jumps (Create Jump)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JumpLookupDTO> createJump(
            @Valid @RequestBody JumpInsertDTO jumpInsertDTO, @AuthenticationPrincipal CustomUserDetails principal
    ) {

        jumpInsertDTO.setUserId(principal.getId());

        JumpLookupDTO createdJump = jumpService.createJump(jumpInsertDTO);

        //Create new URI = api/jumps/{jumpId}
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdJump.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdJump);

    }


    // GET /api/jumps/{id} (Get Single Jump)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<JumpLookupDTO> getJump(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        JumpLookupDTO retrievedJump = jumpService.getJump(id);

        // Confirming Owner after Jump retrieval
        if (!retrievedJump.getUser().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You can not retrieve a jump at another user's profile");
        }

        return ResponseEntity.ok(retrievedJump);

    }


    // PUT /api/jumps/{id} (Update Jump)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JumpLookupDTO> updateJump(
            @PathVariable("id") Long id,
            @Valid @RequestBody JumpUpdateDTO jumpUpdateDTO,
            @AuthenticationPrincipal CustomUserDetails principal
            ) {

        JumpLookupDTO retrievedJump = jumpService.getJump(id);

        // Confirming Owner
        if (!retrievedJump.getUser().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You can not update a jump at another user's profile");
        }

        JumpLookupDTO updatedJump = jumpService.updateJump(retrievedJump.getId(), jumpUpdateDTO);

        return ResponseEntity.ok(updatedJump);

        }


    // DELETE /api/jumps/{id} (Delete Jump)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JumpLookupDTO.MessageResponse> deleteJump(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        JumpLookupDTO deletedJump = jumpService.getJump(id);

        // Confirming Owner
        if (!deletedJump.getUser().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You can not delete a jump at another user's profile");
        }

        jumpService.deleteJump(id);

        return ResponseEntity.ok(new JumpLookupDTO.MessageResponse("Jump Was Successfully Deleted", deletedJump));

    }


    // GET /api/jumps/all (Paginated List)
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<JumpLookupDTO>> getAllJumps(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails principal
            ) {

        Page<JumpLookupDTO> jumpLookupDTOPage = jumpService.getAllJumps(principal.getId(), pageable);

        return ResponseEntity.ok(jumpLookupDTOPage);

    }


    // GET /api/jumps/totalfreefall
    @GetMapping("/totalfreefall")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getTotalFreeFallTime(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        String totalFreeFall = jumpService.getTotalFreefallTime(principal.getId());

        return ResponseEntity.ok(totalFreeFall);

    }



    // GET /api/jumps/totaljumps
    @GetMapping("/totaljumps")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getTotalNumberOfJumps(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        Long totalJumps = jumpService.getTotalNumberOfJumps(principal.getId());

        return ResponseEntity.ok(totalJumps);

    }

    // GET /api/jumps/search (Paginated Search)
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<JumpLookupDTO>> searchJumps(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime jumpDateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime jumpDateTo,
            @RequestParam Jumptype jumptype,
            @PageableDefault Pageable pageable
    ) {

        Page<JumpLookupDTO> searchedJumpsResult = jumpService.searchJumps(principal.getId(), jumpDateFrom, jumpDateTo, jumptype, pageable);

        return ResponseEntity.ok(searchedJumpsResult);

    }

}
