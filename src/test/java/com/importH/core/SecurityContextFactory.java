package com.importH.core;

import com.importH.domain.user.CustomUser;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


@RequiredArgsConstructor
public class SecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final CustomUserDetailsService customUserDetailsService;
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

        CustomUser customUser = new CustomUser(user);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(customUser, "", customUser.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);
        return securityContext;
    }

}
