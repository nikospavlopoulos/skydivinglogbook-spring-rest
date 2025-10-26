package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.dto.lookup.AircraftLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.DropzoneLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumptypeLookupDTO;
import com.nikospavlopoulos.skydivingrest.mapper.AircraftMapper;
import com.nikospavlopoulos.skydivingrest.mapper.DropzoneMapper;
import com.nikospavlopoulos.skydivingrest.mapper.JumptypeMapper;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test covering the StaticDataService
 */

@ActiveProfiles("test")
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
    @Mock
    private JumptypeMapper jumptypeMapper;
    @Mock
    private DropzoneMapper dropzoneMapper;
    @Mock
    private AircraftMapper aircraftMapper;

      // Arrange instances
    Aircraft aircraft1 = new Aircraft(1L, "plane1");
    AircraftLookupDTO aircraft1LookupDTO = new AircraftLookupDTO(aircraft1.getId(), aircraft1.getAircraftName());

    Aircraft aircraft2 = new Aircraft(2L, "plane2");
    AircraftLookupDTO aircraft2LookupDTO = new AircraftLookupDTO(aircraft2.getId(), aircraft2.getAircraftName());

    Aircraft aircraft3 = new Aircraft(3L, "plane3");
    AircraftLookupDTO aircraft3LookupDTO = new AircraftLookupDTO(aircraft3.getId(), aircraft3.getAircraftName());

    Dropzone dropzone1 = new Dropzone(1L, "dropzone1");
    DropzoneLookupDTO dropzone1LookupDTO = new DropzoneLookupDTO(dropzone1.getId(), dropzone1.getDropzoneName());
    Dropzone dropzone2 = new Dropzone(2L, "dropzone2");
    DropzoneLookupDTO dropzone2LookupDTO = new DropzoneLookupDTO(dropzone2.getId(), dropzone2.getDropzoneName());
    Dropzone dropzone3 = new Dropzone(3L, "dropzone3");
    DropzoneLookupDTO dropzone3LookupDTO = new DropzoneLookupDTO(dropzone3.getId(), dropzone3.getDropzoneName());

    Jumptype jumptype1 = new Jumptype(1L, "jumptype1");
    JumptypeLookupDTO jumptype1LookupDTO = new JumptypeLookupDTO(jumptype1.getId(), jumptype1.getJumptypeName());
    Jumptype jumptype2 = new Jumptype(2L, "jumptype2");
    JumptypeLookupDTO jumptype2LookupDTO = new JumptypeLookupDTO(jumptype2.getId(), jumptype2.getJumptypeName());
    Jumptype jumptype3 = new Jumptype(3L, "jumptype3");
    JumptypeLookupDTO jumptype3LookupDTO = new JumptypeLookupDTO(jumptype3.getId(), jumptype3.getJumptypeName());


    @Test
    void findAllAircraft_shouldReturnLookupDTOs() {

        when(aircraftRepository.findAll()).thenReturn(List.of(aircraft1, aircraft2, aircraft3));
        when(aircraftMapper.aircraftToLookupDTO(aircraft1)).thenReturn(aircraft1LookupDTO);
        when(aircraftMapper.aircraftToLookupDTO(aircraft2)).thenReturn(aircraft2LookupDTO);
        when(aircraftMapper.aircraftToLookupDTO(aircraft3)).thenReturn(aircraft3LookupDTO);

        List<AircraftLookupDTO> resultAircraft =  staticDataService.findAllAircraft();

        // Assert
        assertEquals("plane1", resultAircraft.getFirst().getAircraftName());
        assertEquals(1L, resultAircraft.getFirst().getId());
        assertEquals(3L, resultAircraft.getLast().getId());
        assertEquals("plane2", resultAircraft.get(1).getAircraftName());
        assertEquals(List.of(aircraft1LookupDTO, aircraft2LookupDTO, aircraft3LookupDTO), resultAircraft);

        //Verify interactions
        verify(aircraftMapper, times(1)).aircraftToLookupDTO(aircraft1);
        verify(aircraftMapper).aircraftToLookupDTO(aircraft3);
        verify(aircraftRepository, times(1)).findAll();
    }

    @Test
    void findAllDropzones_shouldReturnLookupDTOs() {

        when(dropzoneRepository.findAll()).thenReturn(List.of(dropzone1, dropzone2, dropzone3));
        when(dropzoneMapper.dropzoneToDropzoneLookupDTO(dropzone1)).thenReturn(dropzone1LookupDTO);
        when(dropzoneMapper.dropzoneToDropzoneLookupDTO(dropzone2)).thenReturn(dropzone2LookupDTO);
        when(dropzoneMapper.dropzoneToDropzoneLookupDTO(dropzone3)).thenReturn(dropzone3LookupDTO);

        List<DropzoneLookupDTO> resultDropzone =  staticDataService.findAllDropzones();

        // Assert
        assertEquals("dropzone1", resultDropzone.getFirst().getDropzoneName());
        assertEquals(1L, resultDropzone.getFirst().getId());
        assertEquals(3L, resultDropzone.getLast().getId());
        assertEquals("dropzone2", resultDropzone.get(1).getDropzoneName());
        assertEquals(List.of(dropzone1LookupDTO, dropzone2LookupDTO, dropzone3LookupDTO), resultDropzone);

        //Verify interactions
        verify(dropzoneMapper, times(1)).dropzoneToDropzoneLookupDTO(dropzone1);
        verify(dropzoneMapper).dropzoneToDropzoneLookupDTO(dropzone3);
        verify(dropzoneRepository, times(1)).findAll();
    }

    @Test
    void findAllJumptypes_shouldReturnLookupDTOs() {

        when(jumptypeRepository.findAll()).thenReturn(List.of(jumptype1, jumptype2, jumptype3));
        when(jumptypeMapper.jumptypeToJumptypeLookupDTO(jumptype1)).thenReturn(jumptype1LookupDTO);
        when(jumptypeMapper.jumptypeToJumptypeLookupDTO(jumptype2)).thenReturn(jumptype2LookupDTO);
        when(jumptypeMapper.jumptypeToJumptypeLookupDTO(jumptype3)).thenReturn(jumptype3LookupDTO);

        List<JumptypeLookupDTO> resultJumptype =  staticDataService.findAllJumptypes();

        // Assert
        assertEquals("jumptype1", resultJumptype.getFirst().getJumptypeName());
        assertEquals(1L, resultJumptype.getFirst().getId());
        assertEquals(3L, resultJumptype.getLast().getId());
        assertEquals("jumptype2", resultJumptype.get(1).getJumptypeName());
        assertEquals(List.of(jumptype1LookupDTO, jumptype2LookupDTO, jumptype3LookupDTO), resultJumptype);

        //Verify interactions
        verify(jumptypeMapper, times(1)).jumptypeToJumptypeLookupDTO(jumptype1);
        verify(jumptypeMapper).jumptypeToJumptypeLookupDTO(jumptype3);
        verify(jumptypeRepository, times(1)).findAll();
    }


}
