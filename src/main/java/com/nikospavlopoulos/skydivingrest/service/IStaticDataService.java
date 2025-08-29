package com.nikospavlopoulos.skydivingrest.service;

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

    List<Aircraft> findAllAircraft();

    List<Dropzone> findAllDropzones();

    List<Jumptype> findAllJumptypes();

}
