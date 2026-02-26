package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.AutenticationResponseDto;
import com.amichettinestor.booknext.booknext.dto.AutheticationRequestDto;
import com.amichettinestor.booknext.booknext.dto.RegisterRequestDto;

public interface AuthenticationService {

    AutenticationResponseDto authentication(AutheticationRequestDto requestDto);
    void register(RegisterRequestDto requestDto);
    String confirmToken(String token);
}
