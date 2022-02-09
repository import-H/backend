package com.importH.config.security;

import com.importH.core.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class UserAccount extends org.springframework.security.core.userdetails.User {

    private User user;

    public UserAccount(User user) {
        super(user.getNickname(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole())));
        this.user = user;
    }



}
