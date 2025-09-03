package com.nikospavlopoulos.skydivingrest.specification;

import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides JPA Specifications for filtering Jump entities by user, date range, and jump type.
 */


public class JumpSpecifications {

    public static Specification<Jump> filterJumps(Long userId, LocalDateTime jumpDateFrom, LocalDateTime jumpDateTo, Jumptype jumptype) {

        return (Root<Jump> root,CriteriaQuery<?> query,CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Always filter by UserId
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            // Optional: jumpDateFrom
            if (jumpDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("jumpDate"), jumpDateFrom));
            }

            // Optional: jumpDateTo
            if (jumpDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("jumpDate"), jumpDateTo));
            }

            // Optional: jumptype
            if (jumptype != null) {
                predicates.add(cb.equal(root.get("jumptype"), jumptype));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
