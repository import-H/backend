package com.importH.dto;

import com.importH.domain.User;
import lombok.Getter;

import java.util.List;

@Getter
public class UserLoginResponseDto {
    private final String email;
    private final List<String> roles;

    public UserLoginResponseDto(User user) {
        email = user.getEmail();
        this.roles = user.getRoles();
    }
}
