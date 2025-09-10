package com.nikospavlopoulos.skydivingrest.config;

import com.nikospavlopoulos.skydivingrest.security.*;
import com.nikospavlopoulos.skydivingrest.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configure SecurityFilterChain

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
//    @Autowired
//    private CustomAccessDeniedHandler accessDeniedHandler;
//    @Autowired
//    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(customAuthenticationEntryPoint())
                                .accessDeniedHandler(customAccessDeniedHandler())
                )
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .requestMatchers("/api/auth/register").permitAll()
//                        .requestMatchers("/api/static/**").authenticated()
//                        .requestMatchers("/api/jump/all").authenticated()
                        .requestMatchers("/api/auth/**").permitAll() // for login and register
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/jumps/**").authenticated()
                        .requestMatchers("/api/lookups/**").authenticated() // for static - reference data (dropzones, jumptypes, aircraft)
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/").permitAll()
                );

                //TODO: After Controllers CORS configuration to allow All Methods for the UI
                //TODO: Ensure @PreAuthorize for SKYDIVER & ADMIN

                return http.build();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }


}
