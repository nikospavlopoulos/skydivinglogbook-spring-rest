package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.dto.lookup.AircraftLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.DropzoneLookupDTO;
import com.nikospavlopoulos.skydivingrest.dto.lookup.JumptypeLookupDTO;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;

import java.util.List;

/**
 * Service inteface to handle Static Data
 * Dropzones, Jumptypes, Aircraft
 * Necessary for future dropdown menus
 */

public interface IStaticDataService {

    List<AircraftLookupDTO> findAllAircraft();

    List<DropzoneLookupDTO> findAllDropzones();

    List<JumptypeLookupDTO> findAllJumptypes();

}
