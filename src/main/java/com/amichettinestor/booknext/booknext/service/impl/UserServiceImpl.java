package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.enums.AdminChangeUserStatus;
import com.amichettinestor.booknext.booknext.enums.ManagerChangeUserStatus;
import com.amichettinestor.booknext.booknext.enums.UserStatus;
import com.amichettinestor.booknext.booknext.exception.AdminChangeStatusException;
import com.amichettinestor.booknext.booknext.exception.EmailAlreadyExistsException;
import com.amichettinestor.booknext.booknext.exception.ManagerChangeStatusException;
import com.amichettinestor.booknext.booknext.repository.UserRepository;
import com.amichettinestor.booknext.booknext.service.EmailService;
import com.amichettinestor.booknext.booknext.service.UserService;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));

        return UserResponseDto.builder()
                .address(user.getAddress())
                .name(user.getName())
                .id(user.getId())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .location(LocationDto.builder()
                        .name(user.getLocation().getName())
                        .countryName(user.getLocation().getCountry().getName())
                        .build())
                .build();
    }

    @Override
    public UserResponseDto patchMe(UserRequestDto requestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));

        PatchUtils.copyNonNullProperties(requestDto, user);

        this.userRepository.save(user);

        return UserResponseDto.builder()
                .address(user.getAddress())
                .name(user.getName())
                .lastName(user.getLastName())
                .id(user.getId())
                .email(user.getEmail())
                .location(LocationDto.builder()
                        .name(user.getLocation().getName())
                        .countryName(user.getLocation().getCountry().getName())
                        .build())
                .build();
    }

    @Override
    public void changeMyUsername(ChangeUsernameRequestDto changeUsernameRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));

        if(userRepository.existsByUsername(changeUsernameRequestDto.getNewUsername())) {
            throw new EmailAlreadyExistsException("El usuario ya está en uso");
        }

        user.setUsername(changeUsernameRequestDto.getNewUsername());

        this.userRepository.save(user);
    }

    @Transactional
    public void changeMyPassword(ChangePasswordRequestDto changePasswordRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("El password actual ingresado es incorrecto");
        }

        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getCurrentPassword())) {
            throw new IllegalArgumentException("Los passwords no coinciden");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));

        this.userRepository.save(user);
    }


    @Override
    public void changeMyEmail(ChangeEmailRequestDto changeEmailRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));

        if(userRepository.existsByEmail(changeEmailRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("El email ya está en uso");
        }

        user.setEmail(changeEmailRequestDto.getEmail());
        var savedUser=this.userRepository.save(user);

        //formo el mensaje a enviar por email
        String subject = "Cambio de email";
        String body = """
        Su email se ha actualizado correctamente.
        
        Saludos.
        """;

        // Enviar correo al usuario
        emailService.sendEmail(savedUser.getEmail(), subject, body);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con id" + id));

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .email(user.getEmail())
                .location(LocationDto.builder()
                        .countryName(user.getLocation().getCountry().getName())
                        .name(user.getLocation().getName())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        var users = this.userRepository.findAll();
        return users.stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .lastName(user.getLastName())
                        .address(user.getAddress())
                        .email(user.getEmail())
                        .location(LocationDto.builder()
                                .countryName(user.getLocation().getCountry().getName())
                                .name(user.getLocation().getName())
                                .build())
                        .build())
                .toList();
    }

    @Override
    public void changeStatusByManager(Long id, ManagerChangeStatusUserRequestDto managerChangeStatusRequestDto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con id" + id));

        var requestedStatus = managerChangeStatusRequestDto.getStatus();

        if (requestedStatus != ManagerChangeUserStatus.SUSPENDED) {
            throw new ManagerChangeStatusException("El estado solicitado no puede ser asignado por un Manager");
        }

        user.setStatus(UserStatus.SUSPENDED);

        this.userRepository.save(user);
    }

    @Override
    public void changeStatusByAdmin(Long id, AdminChangeStatusUserRequestDto adminChangeStatusRequestDto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con id" + id));

        var requestedStatus = adminChangeStatusRequestDto.getStatus();

        if (requestedStatus != AdminChangeUserStatus.ACTIVE &&
                requestedStatus != AdminChangeUserStatus.SUSPENDED &&
                requestedStatus != AdminChangeUserStatus.BANNED) {
            throw new AdminChangeStatusException("El estado solicitado no puede ser asignado");
        }

        UserStatus newStatus;
        if (requestedStatus == AdminChangeUserStatus.SUSPENDED) {
            newStatus = UserStatus.SUSPENDED;
        } else if(requestedStatus == AdminChangeUserStatus.ACTIVE) {
            newStatus = UserStatus.ACTIVE;
        }else{
            newStatus = UserStatus.BANNED;
        }

        user.setStatus(newStatus);

        this.userRepository.save(user);

    }
}
