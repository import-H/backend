package com.importH.core.dto.sign;

import com.importH.core.domain.account.Account;
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
