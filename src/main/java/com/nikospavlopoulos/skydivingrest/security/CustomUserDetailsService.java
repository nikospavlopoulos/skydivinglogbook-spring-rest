package com.nikospavlopoulos.skydivingrest.security;

import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndActiveIsTrue(username).orElseThrow(() -> new UsernameNotFoundException("The user with the username: " + username + " is not found"));
        return new CustomUserDetails(user);
    }

}
