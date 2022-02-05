package com.importH.core;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.account.AccountRepository;
import com.importH.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


@RequiredArgsConstructor
public class SecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String nickname = withAccount.value();

        Account account= Account.builder().nickname(nickname)
                .email(nickname + "@email.com")
                .password(passwordEncoder.encode("testtest"))
                .build();

        accountRepository.save(account);

        UserDetails userDetailsService = userService.loadUserByUsername(nickname);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetailsService, userDetailsService.getPassword(), userDetailsService.getAuthorities()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);
        return securityContext;
    }
}
