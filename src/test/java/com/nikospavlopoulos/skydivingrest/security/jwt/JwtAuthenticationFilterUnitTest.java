package com.nikospavlopoulos.skydivingrest.security.jwt;

import com.nikospavlopoulos.skydivingrest.core.enums.Role;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetails;
import com.nikospavlopoulos.skydivingrest.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterUnitTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private IJwtService jwtService;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private FilterChain filterChain;

    // TEST - filter Happy Path

    @Test
    void filter_whenTokenValid_shouldSetAuthenticationAndContinue() throws ServletException, IOException {

        String fakeJwt = "fake-valid-token";

        User user = new User(
                1L, UUID.randomUUID(),
                true,
                "username@test.com",
                "a@123456",
                "Firstname", "Lastname", Role.SKYDIVER
        );

        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(jwtService.extractUsername(fakeJwt)).thenReturn("username@test.com");
        when(jwtService.validateToken(fakeJwt)).thenReturn(true);
        when(customUserDetailsService.loadUserByUsername("username@test.com")).thenReturn(userDetails);

        // Constructs Mock Request with "Authorization" header

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + fakeJwt);

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Invokes the filter

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Assert - doFilter was called & SecurityContext populated

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        assertEquals(userDetails.getUsername(), principal.getUsername());
        assertEquals(userDetails.getAuthorities(), SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        // Verify filter chain continued
        verify(filterChain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }







    // Valid token but user disabled/removed | Token valid, but the user no longer exists in DB (simulate by deleting user). Assert: 401 or 403 depending on your policy; prefer 401 (re-auth required).





    // Filter ordering / unauthenticated endpoints | Requests to /api/auth/login should be permitted without token and should not trigger token validation attempts.






}