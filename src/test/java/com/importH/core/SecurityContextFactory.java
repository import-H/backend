package com.importH.core;

import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;


@RequiredArgsConstructor
public class SecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String nickname = withAccount.value();

        User user = User.builder().nickname(nickname)
                .email(nickname + "@email.com")
                .password(passwordEncoder.encode("testtest"))
                .role(nickname.equals("관리자") ? "ROLE_ADMIN" : "ROLE_USER")
                .weekAgree(true)
                .emailVerified(true)
                .build();

        userRepository.save(user);

        UserDetails userDetailsService = userService.loadUserByUsername(user.getEmail());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetailsService, userDetailsService.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole()))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);
        return securityContext;
    }

}
