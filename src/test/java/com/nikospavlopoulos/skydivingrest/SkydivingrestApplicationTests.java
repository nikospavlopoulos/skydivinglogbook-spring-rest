package com.nikospavlopoulos.skydivingrest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

/**
 * Smoke test for the application using the 'test' profile.
 */

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SkydivingrestApplicationTests {

    // Inject the datasource
    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
    }

    @Test
    void contextLoadsForTestProfile() {    // Spring context will load using 'test' profile
        assertThat(dataSource).isNotNull();
    }

}
