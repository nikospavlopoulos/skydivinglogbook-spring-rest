package com.nikospavlopoulos.skydivingrest.repository;

import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JumptypeRepository extends JpaRepository<Jumptype, Long> {
}
