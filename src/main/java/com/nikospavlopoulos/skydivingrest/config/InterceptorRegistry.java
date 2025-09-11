package com.nikospavlopoulos.skydivingrest.config;

import com.nikospavlopoulos.skydivingrest.rest.logging.LoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring configuration class for registering custom interceptors.
 * Registers the LoggingInterceptor, which logs every incoming request
 * and outgoing response for controller endpoints.
 */
@Configuration
public class InterceptorRegistry implements WebMvcConfigurer {

    /**
     * Register custom interceptors here.
     * The LoggingInterceptor will be executed before and after controller methods
     * for all incoming HTTP requests.
     * @param registry the InterceptorRegistry provided by Spring
     */
    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {

        registry.addInterceptor(new LoggingInterceptor());

    }
}
