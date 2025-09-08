package com.nikospavlopoulos.skydivingrest.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * AccessDeniedHandler only runs after authentication succeeded but authorization failed.
 */

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Avoid potential ClassCastException
        CustomUserDetails principal = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            principal = (CustomUserDetails) authentication.getPrincipal();
        }

        // Log the warning (Handle null)
        if (principal != null) {
            log.warn("Access Denied for user: {}, message: {}", principal.getUsername(), accessDeniedException.getMessage());
        } else {
            log.warn("Access Denied (Insufficient Permissions ), message: {}", accessDeniedException.getMessage());
        }

        // Set HTTP status to 403 - Forbidden.
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Set Content-Type to application/json.
        response.setContentType("application/json; charset=UTF-8");

        // Write a short JSON object in the response body with these fields:
        //timestamp — ISO-8601 time of the error (helps debugging).
        //status — numeric (403).
        //error — short word like "Unauthorized".
        //message — a short, user-safe explanation
        //path — the request URI
        /// "{"timestamp": "{}","status": "{}","error": "Unauthorized", "description": "Invalid or expired authentication token","path": "{}"}" ///

        // TODO: Refactor using Jackson JSON Construction. (Jackson ObjectMapper to serialize a Map<String,Object>.)
        String json;
        if (principal != null) {
            json = String.format("{\"timestamp\": \"%s\",\"status\": \"%d\",\"error\": \"Forbidden\", \"message\": \"Access Denied (Insufficient Permissions)\",\"path\": \"%s\", \"user\": \"%s\"}", java.time.Instant.now().toString(), HttpServletResponse.SC_FORBIDDEN, request.getRequestURI(), principal.getUsername());
        } else {
        json = String.format("{\"timestamp\": \"%s\",\"status\": \"%d\",\"error\": \"Forbidden\", \"message\": \"Access Denied (Insufficient Permissions)\",\"path\": \"%s\"}", java.time.Instant.now().toString(), HttpServletResponse.SC_FORBIDDEN, request.getRequestURI());}

        // Write it in the response
        try {
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Failed to write AccessDenied response", e);
        }
    }

}
