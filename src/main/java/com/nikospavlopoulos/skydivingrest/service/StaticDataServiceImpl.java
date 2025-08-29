package com.nikospavlopoulos.skydivingrest.service;

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

    // Return all static data

    @Override
    @Transactional(readOnly = true)
    public List<Aircraft> findAllAircraft() {
        return aircraftRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dropzone> findAllDropzones() {
        return dropzoneRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jumptype> findAllJumptypes() {
        return jumptypeRepository.findAll();
    }





}
