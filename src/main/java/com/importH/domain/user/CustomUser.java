package com.importH.domain.user;

import com.importH.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class CustomUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CustomUser(User user) {
        super(user.getNickname(), "", List.of(new SimpleGrantedAuthority(user.getRole())));
        this.user = user;
    }



}
