package com.importH.core.dto.sign;

import com.importH.core.domain.account.Account;
import lombok.Getter;

import java.util.List;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String nickName;
    private String roles;

    public UserResponseDto(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.nickName = account.getNickname();
        this.roles = account.getRole();
    }
}