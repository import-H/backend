package com.importH.dto.user;

import com.importH.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String nickName;
    private List<String> roles;
    private Collection<? extends GrantedAuthority> authorities;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.roles = user.getRoles();
        this.authorities = user.getAuthorities();
    }
}