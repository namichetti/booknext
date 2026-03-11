package com.amichettinestor.booknext.booknext.controller;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe() {
        var userResponseDto = this.userService.getMe();
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> patchMe(@Valid @RequestBody UserRequestDto requestDto) {
        var userResponseDto = this.userService.patchMe(requestDto);
        return ResponseEntity.ok(userResponseDto);
    }


    @PatchMapping("/me/username")
    public ResponseEntity<String> changeMyUsername(@Valid @RequestBody ChangeUsernameRequestDto changeUsernameRequestDto) {
        userService.changeMyUsername(changeUsernameRequestDto);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }


    @PatchMapping("/me/password")
    public ResponseEntity<String> changeMyPassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        userService.changeMyPassword(changePasswordRequestDto);
        return ResponseEntity.ok("Password actualizado correctamente");
    }


    @PatchMapping("/me/email")
    public ResponseEntity<String> changeMyEmail(@Valid @RequestBody ChangeEmailRequestDto changeEmailRequestDto) {
        userService.changeMyEmail(changeEmailRequestDto);
        return ResponseEntity.ok("Email actualizado correctamente");
    }


    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<String> changeStatusByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminChangeStatusUserRequestDto adminChangeStatusRequestDto) {
        userService.changeStatusByAdmin(id, adminChangeStatusRequestDto);
        return ResponseEntity.ok("Se ha actualizado el estado del usuario");
    }


    @PatchMapping("/manager/{id}/status")
    public ResponseEntity<String> changeStatusByManager(
            @PathVariable Long id,
            @Valid @RequestBody ManagerChangeStatusUserRequestDto managerChangeStatusRequestDto) {
        userService.changeStatusByManager(id, managerChangeStatusRequestDto);
        return ResponseEntity.ok("Se ha actualizado el estado del usuario");
    }
}
