package com.importH.dto.user;

import com.importH.domain.Account;
import lombok.Getter;

import java.util.List;

@Getter
public class UserLoginResponseDto {
    private final String email;
    private final List<String> roles;

    public UserLoginResponseDto(Account account) {
        email = account.getEmail();
        this.roles = account.getRoles();
    }
}
