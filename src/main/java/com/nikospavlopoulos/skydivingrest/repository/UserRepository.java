package com.nikospavlopoulos.skydivingrest.repository;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsernameAndActiveIsTrue(String username);

    Optional<User> findByIdAndActiveIsTrue(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByUuidAndActiveIsTrue(UUID uuid);


    // The following are for ADMINs only

    Page<User> findAllByActive(boolean active, Pageable pageable);

    Page<User> findAllByRole(Role role, Pageable pageable);

    User getReferenceByUsername(String username);

}
