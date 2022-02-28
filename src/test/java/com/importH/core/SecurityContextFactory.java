package com.importH.core;

import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.service.SignService;
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

    private final SignService signService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String nickname = withAccount.value();

        User user;

        if (!userRepository.existsByNickname(nickname)) {
            user = User.builder().nickname(nickname)
                    .email(nickname + "@email.com")
                    .password(passwordEncoder.encode("testtest"))
                    .role(nickname.equals("관리자") ? "ROLE_ADMIN" : "ROLE_USER")
                    .weekAgree(true)
                    .emailVerified(true)
                    .pathId(nickname.equals("소셜로그인") ? null : nickname)
                    .oauthId(nickname.equals("소셜로그인") ? "social" : null)
                    .build();

            userRepository.save(user);
        } else {
            user = userRepository.findByNickname(nickname).get();
        }

        UserDetails userDetailsService = signService.loadUserByUsername(user.getEmail());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetailsService, userDetailsService.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole()))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);
        return securityContext;
    }

}
