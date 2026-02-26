package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.config.security.SecurityUser;
import com.amichettinestor.booknext.booknext.dto.AutenticationResponseDto;
import com.amichettinestor.booknext.booknext.dto.AutheticationRequestDto;
import com.amichettinestor.booknext.booknext.dto.RegisterRequestDto;
import com.amichettinestor.booknext.booknext.entity.User;
import com.amichettinestor.booknext.booknext.entity.VerificationToken;
import com.amichettinestor.booknext.booknext.enums.Role;
import com.amichettinestor.booknext.booknext.enums.UserStatus;
import com.amichettinestor.booknext.booknext.exception.InvalidVerificationTokenException;
import com.amichettinestor.booknext.booknext.exception.LocationCountryNotFoundException;
import com.amichettinestor.booknext.booknext.exception.UserAlreadyExistsException;
import com.amichettinestor.booknext.booknext.repository.LocationRepository;
import com.amichettinestor.booknext.booknext.repository.VerificationTokenRepository;
import com.amichettinestor.booknext.booknext.repository.UserRepository;
import com.amichettinestor.booknext.booknext.service.AuthenticationService;
import com.amichettinestor.booknext.booknext.service.EmailService;
import com.amichettinestor.booknext.booknext.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    private static final Logger logger = LogManager.getLogger(AuthenticationServiceImpl.class);
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager  authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.confirmation.link}")
    private String confirmationLink;

    @Override
    @Transactional(readOnly=true)
    public AutenticationResponseDto authentication(AutheticationRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(),
                        requestDto.getPassword()));

        //Verifica si existe el username en la base de datos.
        var user = this.userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el user "+requestDto.getUsername()));

        //Si existe entonces generamos el token
        var token =this.jwtService.generatetoken(new SecurityUser(user));

        //Devolvemos el DTO de repuesta para la autenticación con el token
        return AutenticationResponseDto
                .builder()
                .accessToken(token)
                .build();
    }

    @Override
    public void register(RegisterRequestDto requestDto) {
        //Verifico si el username y/o email ya está registrado
        boolean usernameExists = userRepository
                .findByUsername(requestDto.getUsername())
                .isPresent();

        boolean emailExists = userRepository
                .existsByEmail(requestDto.getEmail());

        if (usernameExists || emailExists) {
            if (usernameExists) {
                logger.error("El username ya existe");
            }else{
                logger.error("El email ya está registrado");
            }
            throw new UserAlreadyExistsException("Algunos de los datos ingresados ya están en uso.");
        }

        //Verifico si la Location y Country existen, y que estén asociadas.
        var location = this.locationRepository.findByNameAndCountryName(requestDto.getLocation().getName(),
                requestDto.getLocation().getCountryName())
                .orElseThrow(()->new LocationCountryNotFoundException("Locación y/o país no existe/n"));

        var user = new User();
        user.setEmail(requestDto.getEmail());
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setName(requestDto.getUsername());
        user.setLastName(requestDto.getLastName());
        user.setRole(Role.USER);
        user.setAddress(requestDto.getAddress());
        user.setLocation(location);
        user.setStatus(UserStatus.INACTIVE); //El usuario debe confirmar su cuenta.

        var savedUser = this.userRepository.save(user);


        //Creamos token de expiración
        String token = UUID.randomUUID().toString();

        var verificationToken = VerificationToken.builder()
                .user(savedUser)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        verificationTokenRepository.save(verificationToken);

        //formo el mensaje a enviar por email
        String subject = "Confirmá tu cuenta";
        String link=this.confirmationLink + "/auth/confirm?token=" + token;
        String body = """
        Gracias por registrarte.
        
        Para activar tu cuenta, haz click en el siguiente enlace:
        %s
        
        Este enlace expira en 5 minutos.
        """.formatted(link);

        // Enviar correo al usuario
        emailService.sendEmail(savedUser.getEmail(), subject, body);
    }

    @Override
    public String confirmToken(String token) {
        var verificationToken=this.verificationTokenRepository.findByToken(token)
                .orElseThrow(()->{
                    logger.info("Token ha expirado.");
                    return new InvalidVerificationTokenException("No pudimos confirmar tu cuenta. Por favor, intentá nuevamente.");
                });

        if(verificationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            logger.error("El token ingresado ya expiró.");
            throw new InvalidVerificationTokenException("No pudimos confirmar tu cuenta. Por favor, intentá nuevamente.");
        }

        if(verificationToken.getUser().getStatus().equals(UserStatus.ACTIVE)){
            return "El usuario ya ha sido confirmado";
        }

        verificationToken.getUser().setStatus(UserStatus.ACTIVE);
        //Guardo al usuario con el nuevo estado
        this.userRepository.save(verificationToken.getUser());

        return "Usuario confirmado.";
    }

}
