package com.nikospavlopoulos.skydivingrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SkydivingrestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkydivingrestApplication.class, args);
    }

}
