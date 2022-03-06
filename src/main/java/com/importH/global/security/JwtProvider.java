package com.importH.global.security;

import com.importH.domain.user.CustomUser;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.token.TokenDto;
import com.importH.global.error.exception.SecurityException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

import static com.importH.global.error.code.SecurityErrorCode.AUTHENTICATION_ENTRYPOINT;


/**
 * Json Web Token (Jwt) 생성 및 유효성 검증을 하는 컴포넌트
 * Claim : 회원을 구분할 수 있는 값을 세팅
 * resolveToken : header 에 세팅된 토큰값을 가져와서 유효성을 검사
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    public static final String ROLES = "roles";
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 30분
    private Long accessTokenValidTime = 24 * 60 * 60 * 1000L; // 30 min // 24 hours
    private Long refreshTokenValidTime = 145 * 24 * 60 * 60 * 1000L; // 14day

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //JWT 토큰 생성
    public TokenDto createToken(User user) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getId())); // JWT PALYLOAD 에 저장되는 정보단위
        claims.put(ROLES, user.getRole());

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNew(isNewUser(user))
                .build();
    }

    private boolean isNewUser(User user) {
        return user.getPathId() == null;
    }

    // Jwt 로 인증정보를 조회
/*    public Authentication getAuthentication(String token) {

        // JWT 에서 CLAIMS 추출
        Claims claims = parseClaims(token);

        if (claims.get(ROLES) == null) {
            throw new SecurityException(AUTHENTICATION_ENTRYPOINT);
        }

        CustomUser customUser = (CustomUser) userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(customUser, "", customUser.getAuthorities());
    }*/

    // jwt 에서 회원 구분 PK 추출
    public Claims parseClaims(String token) {

        try {
            return Jwts.parser().
                    setSigningKey(secretKey).
                    parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException : ", e);
            return e.getClaims();
        }
    }

    // HTTP request 의 Header 에서 Token Parsing -> "X-AUTH-TOKEN: jwt"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // Jwt 의 유효성 및 만료일자 확인
    public boolean validationToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (java.lang.SecurityException | MalformedJwtException | SignatureException e) {
            log.error("잘못된 Jwt 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }
        return false;
    }

}
