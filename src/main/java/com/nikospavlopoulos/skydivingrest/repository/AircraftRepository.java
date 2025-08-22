package com.nikospavlopoulos.skydivingrest.repository;

import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
}
