package com.importH.core.dto.sign;

import com.importH.core.domain.user.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String nickName;
    private String roles;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickname();
        this.roles = user.getRole();
    }
}