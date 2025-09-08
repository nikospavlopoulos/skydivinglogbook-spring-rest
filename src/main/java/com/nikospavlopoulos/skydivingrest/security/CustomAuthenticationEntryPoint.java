package com.nikospavlopoulos.skydivingrest.security;

import com.nikospavlopoulos.skydivingrest.core.exceptions.UnauthorizedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // Log the warning
        log.warn("Access Denied (Invalid or Expired JWT), message: {}",authException.getMessage());

        // Set HTTP status to 401 - Unauthorized.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Set Content-Type to application/json.
        response.setContentType("application/json; charset=UTF-8");

        // Write a short JSON object in the response body with these fields:
            //timestamp — ISO-8601 time of the error (helps debugging).
            //status — numeric (401).
            //error — short word like "Unauthorized".
            //message — a short, user-safe explanation
            //path — the request URI
        /// "{"timestamp": "{}","status": "{}","error": "Unauthorized", "description": "Invalid or expired authentication token","path": "{}", "user": "{}"}" ///

        String json = String.format("{\"timestamp\": \"%s\",\"status\": \"%d\",\"error\": \"Unauthorized\", \"message\": \"Invalid or expired authentication token\",\"path\": \"%s\"}", java.time.Instant.now().toString(), HttpServletResponse.SC_UNAUTHORIZED, request.getRequestURI());

        // Write it in the response
        try {
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Failed to write Unauthorized response", e);
        }

    }
}
