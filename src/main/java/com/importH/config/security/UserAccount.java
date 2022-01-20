package com.importH.config.security;

import com.importH.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserAccount extends User {

    private Account account;

    public UserAccount(Account account) {
        super(account.getNickName(),account.getPassword(), account.getRoles()
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        this.account = account;
    }



}