package com.amichettinestor.booknext.booknext.mapper;

import com.amichettinestor.booknext.booknext.dto.LocationRequestDto;
import com.amichettinestor.booknext.booknext.dto.UserRequestDto;
import com.amichettinestor.booknext.booknext.dto.UserResponseDto;
import com.amichettinestor.booknext.booknext.entity.Location;
import com.amichettinestor.booknext.booknext.entity.User;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserResponseDto toDto(User user){
        return UserResponseDto.builder()
                .address(user.getAddress())
                .name(user.getName())
                .id(user.getId())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .location(LocationRequestDto.builder()
                        .name(user.getLocation().getName())
                        .countryName(user.getLocation().getCountry().getName())
                        .build())
                .build();


    }

    public void patchFromDto(User user, Location location, UserRequestDto requestDto){
        user.setLocation(location);
        PatchUtils.copyNonNullProperties(requestDto, user);
    }

    public List<UserResponseDto> toDtoList(List<User> users) {
        return users.stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .lastName(user.getLastName())
                        .address(user.getAddress())
                        .email(user.getEmail())
                        .location(LocationRequestDto.builder()
                                .countryName(user.getLocation().getCountry().getName())
                                .name(user.getLocation().getName())
                                .build())
                        .build())
                .toList();
    }
}
