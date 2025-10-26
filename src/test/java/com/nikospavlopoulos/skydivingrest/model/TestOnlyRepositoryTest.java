package com.nikospavlopoulos.skydivingrest.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public interface TestOnlyRepositoryTest extends JpaRepository<TestOnlyEntityTest, Long> {


}
