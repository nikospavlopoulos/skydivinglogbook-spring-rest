package com.nikospavlopoulos.skydivingrest.repository;

import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DropzoneRepository extends JpaRepository<Dropzone, Long> {
}
