package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.enums.AdminChangeUserStatus;
import com.amichettinestor.booknext.booknext.enums.ManagerChangeUserStatus;
import com.amichettinestor.booknext.booknext.enums.UserStatus;
import com.amichettinestor.booknext.booknext.exception.*;
import com.amichettinestor.booknext.booknext.mapper.UserMapper;
import com.amichettinestor.booknext.booknext.repository.LocationRepository;
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

import static com.amichettinestor.booknext.booknext.util.Constants.BODY_CHANGE_EMAIL;
import static com.amichettinestor.booknext.booknext.util.Constants.SUBJECT_CHANGE_EMAIL;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LocationRepository locationRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));
        return this.userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto patchMe(UserRequestDto requestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario " + username));

        var location = this.locationRepository.findByNameAndCountryName(
                        requestDto.getLocation().getName(),
                        requestDto.getLocation().getCountryName())
                .orElseThrow(()->new LocationNotFound("No se encontró la localidad "
                        +requestDto.getLocation().getName()+ " del país "
                        + requestDto.getLocation().getCountryName()));

        this.userMapper.patchFromDto(user,location,requestDto);
        this.userRepository.save(user);
        return this.userMapper.toDto(user);
    }

    @Override
    @Transactional
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

        if (!changePasswordRequestDto.getNewPassword().equals(
                changePasswordRequestDto.getConfirmationNewPassword())) {
            throw new PasswordsDontMatchException("Los passwords no coinciden");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));

        this.userRepository.save(user);
    }


    @Override
    @Transactional
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
        // Enviar correo al usuario
        emailService.sendEmail(savedUser.getEmail(), SUBJECT_CHANGE_EMAIL, BODY_CHANGE_EMAIL);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con id " + id));
        return this.userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        var users = this.userRepository.findAll();
        return this.userMapper.toDtoList(users);
    }

    @Override
    @Transactional
    public void changeStatusByManager(Long id, ManagerChangeStatusUserRequestDto managerChangeStatusRequestDto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con id " + id));

        var requestedStatus = managerChangeStatusRequestDto.getStatus();
        UserStatus newStatus;
        if(requestedStatus == ManagerChangeUserStatus.SUSPENDED){
            newStatus = UserStatus.SUSPENDED;
        }else{
            newStatus = UserStatus.INACTIVE;
        }

        user.setStatus(newStatus);

        this.userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeStatusByAdmin(Long id, AdminChangeStatusUserRequestDto adminChangeStatusRequestDto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con id" + id));

        var requestedStatus = adminChangeStatusRequestDto.getStatus();

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
