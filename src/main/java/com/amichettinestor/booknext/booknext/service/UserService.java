package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.*;

import java.util.List;

public interface UserService {
    UserResponseDto getMe();

    UserResponseDto patchMe(UserRequestDto requestDto);

    void changeMyUsername(ChangeUsernameRequestDto changeUsernameRequestDto);

    void changeMyPassword(ChangePasswordRequestDto changePasswordRequestDto);

    void changeMyEmail(ChangeEmailRequestDto changeEmailRequestDto);

    UserResponseDto findById(Long id);

    List<UserResponseDto> findAll();

    void changeStatusByManager(Long id, ManagerChangeStatusUserRequestDto managerChangeStatusRequestDto);

    void changeStatusByAdmin(Long id, AdminChangeStatusUserRequestDto adminChangeStatusRequestDto);
}
