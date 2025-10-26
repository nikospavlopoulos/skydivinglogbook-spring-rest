package com.nikospavlopoulos.skydivingrest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.test.context.ActiveProfiles;

/**
 * A test only entity to test Abstract Class
 * auditing timestamps
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ActiveProfiles("test")
@Table(name = "test_only_table")
public class TestOnlyEntityTest extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_name")
    private String name;
}
