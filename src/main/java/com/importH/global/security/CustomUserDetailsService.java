package com.importH.global.security;

import com.importH.domain.user.CustomUser;
import com.importH.domain.user.entity.User;
import com.importH.global.error.exception.SecurityException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.importH.global.error.code.SecurityErrorCode.AUTHENTICATION_ENTRYPOINT;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JwtProvider jwtProvider;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {

        Claims claims = jwtProvider.parseClaims(token);

        if (claims.get("roles") == null) {
            throw new SecurityException(AUTHENTICATION_ENTRYPOINT);
        }

        return new CustomUser(toUser(claims));
    }

    private User toUser(Claims claims) {
        return User.builder()
                .id(Long.valueOf(claims.getSubject()))
                .role(String.valueOf(claims.get("roles")))
                .build();
    }

    public Authentication getAuthentication(String token) {

        // JWT 에서 CLAIMS 추출
        Claims claims = jwtProvider.parseClaims(token);

        if (claims.get("roles") == null) {
            throw new SecurityException(AUTHENTICATION_ENTRYPOINT);
        }

        CustomUser customUser = (CustomUser) loadUserByUsername(token);
        return new UsernamePasswordAuthenticationToken(customUser, "", customUser.getAuthorities());
    }
}
