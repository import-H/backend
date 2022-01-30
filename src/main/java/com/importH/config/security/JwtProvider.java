package com.importH.config.security;

import com.importH.core.domain.account.Account;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.error.exception.JwtException;
import com.importH.core.error.exception.UserException;
import com.importH.core.domain.account.AccountRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.importH.core.error.code.JwtErrorCode.AUTHENTICATION_ENTRYPOINT;


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
    @Value("${spring.jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 30분
    private Long accessTokenValidTime = 1 * 30 * 1000L; // 30 sec
    private Long refreshTokenValidTime = 14 * 24 * 60 * 60 * 1000L; // 14day

    private final AccountRepository accountRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //JWT 토큰 생성
    public TokenDto createToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT PALYLOAD 에 저장되는 정보단위
        claims.put(ROLES, roles);

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
                .build();
    }

    // Jwt 로 인증정보를 조회
    public Authentication getAuthentication(String token) {

        // JWT 에서 CLAIMS 추출
        Claims claims = parseClaims(token);

        if (claims.get(ROLES) == null) {
            throw new JwtException(AUTHENTICATION_ENTRYPOINT);
        }

        // 권한 정보가 없음
        Account account = accountRepository.findByEmail(claims.getSubject()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USERID));
        return new UsernamePasswordAuthenticationToken(new UserAccount(account), "", account.getRoles()
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
    }

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
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
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
