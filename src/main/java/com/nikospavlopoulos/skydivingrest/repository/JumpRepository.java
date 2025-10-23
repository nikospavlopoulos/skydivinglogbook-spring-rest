package com.nikospavlopoulos.skydivingrest.repository;

import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JumpRepository extends JpaRepository<Jump, Long>, JpaSpecificationExecutor<Jump> {

    Optional<Jump> findByUuid(UUID uuid);

    // Get all user's jumps (Page)
    Page<Jump> findAllByUserId(Long userId, Pageable pageable);

    // Search query Date
    Page<Jump> findJumpByUserIdAndJumpDateBetween(Long user_id, @PastOrPresent LocalDateTime jumpDateFrom, @PastOrPresent LocalDateTime jumpDateTo, Pageable pageable);

    // Search query Date
    Page<Jump> findJumpByUserIdAndJumptype(Long userId, Jumptype jumptype, Pageable pageable);

    // Calculate Skydiver's jump's ordinal number (based on date and then on jump id - correct)
    long countByUserIdAndJumpDateLessThanEqualAndIdLessThanEqual(Long userId, LocalDateTime jumpDateIsLessThan, Long idIsLessThan);

    // Calculate total freefall Time (seconds)
    /// Querydraft: SELECT SUM(jump.freefFallDuration) FROM jump WHERE user_id = ?;
    /// JPQLdraft: SELECT SUM(jump.freeFallDuration) FROM Jump jump WHERE jump.user.id = :userId
    @Query("SELECT SUM(jump.freeFallDuration) FROM Jump jump WHERE jump.user.id = :userId")
    Long sumTotalFreeFallDurationByUser(@Param("userId") Long userId);

    // Calculate Total User's Jumps
    long countJumpByUserId(Long userId);


}
