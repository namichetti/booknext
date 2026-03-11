package com.amichettinestor.booknext.booknext.controller;


import com.amichettinestor.booknext.booknext.dto.AutenticationResponseDto;
import com.amichettinestor.booknext.booknext.dto.AutheticationRequestDto;
import com.amichettinestor.booknext.booknext.dto.RegisterRequestDto;
import com.amichettinestor.booknext.booknext.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<AutenticationResponseDto> login(@Valid @RequestBody AutheticationRequestDto
                                                                      requestDto){
        return ResponseEntity.ok(this.service.authentication(requestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto requestDto){
        this.service.register(requestDto);
        return ResponseEntity.ok("El registro se completó exitosamente. Se le ha enviado un email de confirmación");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token){
        var message= this.service.confirmToken(token);
        return ResponseEntity.ok(message);
    }
}

