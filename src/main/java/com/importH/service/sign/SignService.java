package com.importH.service.sign;

import com.importH.config.security.JwtProvider;
import com.importH.config.security.UserAccount;
import com.importH.domain.Account;
import com.importH.domain.RefreshToken;
import com.importH.dto.jwt.TokenDto;
import com.importH.dto.jwt.TokenRequestDto;
import com.importH.dto.jwt.TokenResponseDto;
import com.importH.dto.sign.UserSignUpRequestDto;
import com.importH.error.code.JwtErrorCode;
import com.importH.error.exception.JwtException;
import com.importH.error.exception.UserException;
import com.importH.repository.RefreshTokenRepository;
import com.importH.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.importH.error.code.UserErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository tokenRepository;

    public Long signup(UserSignUpRequestDto userSignUpRequestDto) {
        if (userRepository.findByEmail(userSignUpRequestDto.getEmail()).orElse(null) == null) {
            return userRepository.save(userSignUpRequestDto.toEntity()).getId();
        }
        throw new UserException(USER_EMAIL_DUPLICATED);
    }


    public TokenDto login(String email, String password) {
        Account account = userRepository.findByEmail(email).orElseThrow(() -> new UserException(EMAIL_LOGIN_FAILED));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new UserException(EMAIL_LOGIN_FAILED);
        }

        // AccessToken, RefreshToken 발급
        TokenDto tokenDto = jwtProvider.createToken(account.getEmail(), account.getRoles());

        // RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(account.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        tokenRepository.save(refreshToken);
        return tokenDto;
    }
    public TokenResponseDto reissue(TokenRequestDto tokenRequestDto) {

        // 만료된  refresh token 에러
        if (!jwtProvider.validationToken(tokenRequestDto.getRefreshToken())) {
            throw new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID);
        }

        // AccessToken 에서 email(pk) 가져오기
        String accessToken = tokenRequestDto.getAccessToken();
        UserAccount userAccount = (UserAccount) jwtProvider.getAuthentication(accessToken).getPrincipal();

        // user pk로 유저 검색 / RefreshToken 이 없음
        Account account = userRepository.findByEmail(userAccount.getAccount().getEmail()).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
        RefreshToken refreshToken = tokenRepository.findByKey(account.getId()).orElseThrow(() -> new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID));

        // 리프레시 토큰 불일치
        if (!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID);
        }

        // AccessToken, RefreshToken 재발급 , 저장
        TokenDto newToken = jwtProvider.createToken(account.getEmail(), account.getRoles());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newToken.getRefreshToken());
        tokenRepository.save(updateRefreshToken);

        return TokenResponseDto.builder().accessToken(newToken.getAccessToken()).refreshToken(newToken.getRefreshToken()).build();
    }
}
