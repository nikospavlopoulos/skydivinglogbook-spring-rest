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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the Service inteface to handle Static Data
 * Dropzones, Jumptypes, Aircraft
 * Necessary for future dropdown menus
 * Implements findALl methods
 */

@Service
@AllArgsConstructor
public class StaticDataServiceImpl implements IStaticDataService{

    // Initialize static data repositories.

    private AircraftRepository aircraftRepository;
    private DropzoneRepository dropzoneRepository;
    private JumptypeRepository jumptypeRepository;

    private AircraftMapper aircraftMapper;
    private DropzoneMapper dropzoneMapper;
    private JumptypeMapper jumptypeMapper;

    // Return all static data
    /*
    TODO: Change Lists to Paginated Lists for better handling of big amount of entries. Also adjust tests
     */

    @Override
    @Transactional(readOnly = true)
    public List<AircraftLookupDTO> findAllAircraft() {
        return aircraftRepository.findAll()
                .stream()
                .map(aircraftMapper::aircraftToLookupDTO)
                .collect(Collectors.toList())                ;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DropzoneLookupDTO> findAllDropzones() {
        return dropzoneRepository.findAll()
                .stream().map(dropzoneMapper::dropzoneToDropzoneLookupDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JumptypeLookupDTO> findAllJumptypes() {
        return jumptypeRepository.findAll()
                .stream().map(jumptypeMapper::jumptypeToJumptypeLookupDTO)
                .collect(Collectors.toList());
    }


}
