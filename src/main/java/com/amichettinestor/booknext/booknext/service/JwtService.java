package com.amichettinestor.booknext.booknext.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String extractUsername(String token);

    boolean istokenValid(String token, UserDetails userDetails);

     String generatetoken(UserDetails userDetails);
}
