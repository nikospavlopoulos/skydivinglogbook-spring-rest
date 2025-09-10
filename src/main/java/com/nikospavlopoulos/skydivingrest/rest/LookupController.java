package com.nikospavlopoulos.skydivingrest.rest;

import com.nikospavlopoulos.skydivingrest.dto.lookup.AircraftLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.DropzoneLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumptypeLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetails;
import com.nikospavlopoulos.skydivingrest.service.IStaticDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/lookups")
public class LookupController {

    private final IStaticDataService staticDataService;

    @GetMapping("/aircraft")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AircraftLookupDTO>> allAircraft(
            ) {
        return ResponseEntity.ok(staticDataService.findAllAircraft());

        // TODO: log message

    }

    @GetMapping("/dropzones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DropzoneLookupDTO>> allDropzones(
    ) {
        return ResponseEntity.ok(staticDataService.findAllDropzones());

        // TODO: log message

    }

    @GetMapping("/jumptypes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<JumptypeLookupDTO>> allJumptypes(
    ) {
        return ResponseEntity.ok(staticDataService.findAllJumptypes());

        // TODO: log message

    }

}
