package com.nikospavlopoulos.skydivingrest.security.jwt;

import com.nikospavlopoulos.skydivingrest.security.CustomUserDetails;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts every HTTP request once.
 * It extracts and validates the JWT (JSON Web Token) from the Authorization header.
 * If token is valid, it loads the corresponding user details and places an
 * {@link Authentication} object into the {@link SecurityContextHolder}.
 * If token is missing or invalid, the request continues without authentication or triggers a 401 error.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // read the Authorization: Bearer <token> header
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTH_HEADER);
        final String jwt;
        final String username;
        final boolean isValid;

        String path = request.getRequestURI();


        // Skip JWT validation for public endpoints
        if (path.startsWith("/api/auth")
                || (path.equals("/api/users") && "POST".equalsIgnoreCase(request.getMethod()))
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/h2-console/")) {
            filterChain.doFilter(request, response);
            return;
        }


        // Short-circuit if header missing or not Bearer
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extract token String (trim out "Bearer ")

        jwt = authHeader.substring(BEARER_PREFIX.length()).trim();

        // Skip if already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Call JwtService (extract Username & Validate)
            username = jwtService.extractUsername(jwt);
            isValid = jwtService.validateToken(jwt);
        } catch (JwtException e) {
            log.warn("Token is not valid for uri = {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
            return;
//            throw new BadCredentialsException("Invalid or Expired JWT");
        }

        if (username == null || !isValid) {
            log.warn("Invalid JWT for uri = {}", request.getRequestURI());
            throw new BadCredentialsException("Invalid or Expired JWT");
        }

        // Build Authentication and attach details
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                null,
                userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);


        // Continue filter chain
        filterChain.doFilter(request, response);

    }

}
