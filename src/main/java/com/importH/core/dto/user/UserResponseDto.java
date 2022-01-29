package com.importH.core.dto.user;

import com.importH.core.entity.Account;
import lombok.Getter;

import java.util.List;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String nickName;
    private List<String> roles;

    public UserResponseDto(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.nickName = account.getNickName();
        this.roles = account.getRoles();
    }
}