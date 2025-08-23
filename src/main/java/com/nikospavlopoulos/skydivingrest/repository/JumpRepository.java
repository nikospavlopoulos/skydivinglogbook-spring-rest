package com.nikospavlopoulos.skydivingrest.repository;

import com.nikospavlopoulos.skydivingrest.model.Jump;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JumpRepository extends JpaRepository<Jump, Long> {
}
