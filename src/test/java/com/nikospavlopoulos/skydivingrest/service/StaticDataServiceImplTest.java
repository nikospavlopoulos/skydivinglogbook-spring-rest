package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.AircraftRepository;
import com.nikospavlopoulos.skydivingrest.repository.DropzoneRepository;
import com.nikospavlopoulos.skydivingrest.repository.JumptypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test covering the StaticDataService
 */

@ExtendWith(MockitoExtension.class)
class StaticDataServiceImplTest {

    @InjectMocks
    private StaticDataServiceImpl staticDataService;

    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private DropzoneRepository dropzoneRepository;
    @Mock
    private JumptypeRepository jumptypeRepository;

    // Arrange instances
    Aircraft aircraft1 = new Aircraft(1L, "plane1");
    Aircraft aircraft2 = new Aircraft(2L, "plane2");
    Aircraft aircraft3 = new Aircraft(3L, "plane3");

    Dropzone dropzone1 = new Dropzone(1L, "dropzone1");
    Dropzone dropzone2 = new Dropzone(2L, "dropzone2");
    Dropzone dropzone3 = new Dropzone(3L, "dropzone3");

    Jumptype jumptype1 = new Jumptype(1L, "jumptype1");
    Jumptype jumptype2 = new Jumptype(2L, "jumptype2");
    Jumptype jumptype3 = new Jumptype(3L, "jumptype3");

    @Test
    void findAllMethods_shouldReturnAllStaticData() {

        // Stub values

        when(aircraftRepository.findAll()).thenReturn(List.of(aircraft1, aircraft2, aircraft3));

        when(dropzoneRepository.findAll()).thenReturn(List.of(dropzone1,dropzone2, dropzone3));

        when(jumptypeRepository.findAll()).thenReturn(List.of(jumptype1, jumptype2, jumptype3));

        // Asserts

        assertEquals(3, staticDataService.findAllAircraft().size());
        assertEquals(3, staticDataService.findAllDropzones().size());
        assertEquals(3, staticDataService.findAllJumptypes().size());
    }
}