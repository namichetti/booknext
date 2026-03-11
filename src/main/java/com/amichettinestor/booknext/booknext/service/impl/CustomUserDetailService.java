package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.config.security.SecurityUser;
import com.amichettinestor.booknext.booknext.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user= userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el user "+username));
        return new SecurityUser(user);
    }
}
