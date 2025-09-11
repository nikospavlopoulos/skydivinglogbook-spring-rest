package com.nikospavlopoulos.skydivingrest.rest.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts all HTTP requests to log basic request and response information.
 * This interceptor logs:
 * - Incoming HTTP method and URL
 * - The controller handler that will process the request
 * - HTTP status after the request is completed
 * - Optional exceptions thrown during request handling
 */
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    // Called before the controller handles the request.
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        log.info("Incoming Request: {} {}", request.getMethod(), request.getRequestURI());
        log.info("Handler: {}", handler);

        return true;
    }

    // Called after the request is completed, after the response is sent
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {

        log.info("Request completed: {} {} -> Status: {}", request.getMethod(), request.getRequestURI(), response.getStatus());

        if (exception != null) {
            log.error("Exception Occurred while processing request", exception);
        }
    }

}
