package com.nikospavlopoulos.skydivingrest.model;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;



@DataJpaTest
@ActiveProfiles("test")
@Transactional
class AbstractEntityTest {

    @Autowired
    private TestOnlyRepositoryTest testOnlyRepositoryTest;

    // Test when new instance is created and the timestamps are null.
    // Both createdAt and updatedAt are set, and createdAt equals updatedAt

    @Test
    void instanceCreateTest() {

        TestOnlyEntityTest testEntity = new TestOnlyEntityTest();
        testEntity.setName("firstName");

        TestOnlyEntityTest savedEntity = testOnlyRepositoryTest.saveAndFlush(testEntity);

        TestOnlyEntityTest foundEntity = testOnlyRepositoryTest.findById(savedEntity.getId()).orElseThrow();

        //Assert
        assertThat(foundEntity.getCreatedAt()).isNotNull();
        assertThat(foundEntity.getUpdatedAt()).isNotNull();
        assertThat(foundEntity.getUpdatedAt()).isEqualTo(foundEntity.getCreatedAt());
    }


    // Test when instance is updated, only updatedAt is changed, and the createdAt remains unchanged

    @Test
    void instanceUpdateTest() throws InterruptedException {
        TestOnlyEntityTest testEntity = new TestOnlyEntityTest();
        testEntity.setName("initialName");

        TestOnlyEntityTest savedEntity = testOnlyRepositoryTest.saveAndFlush(testEntity);

        LocalDateTime createdAtInitial = savedEntity.getCreatedAt();
        LocalDateTime updatedAtInitial = savedEntity.getUpdatedAt();

        // Wait two seconds for update
        Thread.sleep(500);
        testEntity.setName("updatedName");

        TestOnlyEntityTest updatedEntity = testOnlyRepositoryTest.saveAndFlush(testEntity);

        TestOnlyEntityTest foundEntity = testOnlyRepositoryTest.findById(updatedEntity.getId()).orElseThrow();

        //Assert different createdAt != updatedAt
        //Assert different updatedAt != updatedAtInitial
        assertThat(foundEntity.getUpdatedAt()).isNotEqualTo(foundEntity.getCreatedAt());
        assertThat(foundEntity.getUpdatedAt()).isNotEqualTo(updatedAtInitial);

        //Assert createdAt unchanged
        assertThat(foundEntity.getCreatedAt()).isEqualTo(createdAtInitial);
    }
}