package com.importH.core.service;

import com.importH.config.security.JwtProvider;
import com.importH.core.domain.user.User;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.domain.token.RefreshTokenRepository;
import com.importH.core.domain.token.RefreshToken;
import com.importH.core.dto.sign.LoginDto;
import com.importH.core.dto.sign.UserSignUpRequestDto;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.exception.JwtException;
import com.importH.core.error.exception.UserException;
import com.importH.core.domain.user.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static com.importH.core.error.code.UserErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository tokenRepository;

    /** 회원가입 */
    @Transactional
    public Long signup(UserSignUpRequestDto userSignUpRequestDto) {

        validateSignup(userSignUpRequestDto);

        User user = userSignUpRequestDto.toEntity();
        user.setPassword(passwordEncoder.encode(userSignUpRequestDto.getPassword()));

        return saveUser(user).getId();
    }

    private void validateSignup(UserSignUpRequestDto userSignUpRequestDto) {
        passwordCheck(userSignUpRequestDto.getPassword(), userSignUpRequestDto.getConfirmPassword());
        duplicatedEmail(userSignUpRequestDto.getEmail());
        duplicatedNickname(userSignUpRequestDto.getNickname());
    }

    private void passwordCheck(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new UserException(USER_PASSWORD_CHECK);
        }
    }

    private void duplicatedNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(USER_NICKNAME_DUPLICATED);
        }
    }

    private void duplicatedEmail(String email) {
        if (userRepository.findByEmail(email).orElse(null) != null) {
            throw new UserException(USER_EMAIL_DUPLICATED);
        }
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }


    /** 로그인 */
    @Transactional
    public LoginDto.Response login(String email, String password) {
        User user = getAccount(email);
        validatePassword(password, user);
        
        TokenDto tokenDto = createToken(user);
        RefreshToken refreshToken = getRefreshToken(user);

        saveRefreshToken(user, tokenDto, refreshToken);

        return LoginDto.Response.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    private void saveRefreshToken(User user, TokenDto tokenDto, RefreshToken refreshToken) {
        if (refreshToken == null) {
            RefreshToken newRefreshToken = RefreshToken.create(user.getId(), tokenDto.getRefreshToken());
            saveRefreshToken(newRefreshToken);
        } else {
            refreshToken.updateToken(tokenDto.getRefreshToken());
        }
    }

    private void saveRefreshToken(RefreshToken newRefreshToken) {
        tokenRepository.save(newRefreshToken);
    }

    private RefreshToken getRefreshToken(User user) {
        return tokenRepository.findByKey(user.getId()).orElse(null);
    }

    private TokenDto createToken(User user) {
        return jwtProvider.createToken(user.getEmail(), user.getRole());
    }

    private void validatePassword(String password, User user) {
        if (!isMatchPassword(password, user.getPassword())) {
            throw new UserException(EMAIL_LOGIN_FAILED);
        }
    }

    private boolean isMatchPassword(String password, String accountPassword) {
        return passwordEncoder.matches(password, accountPassword);
    }

    private User getAccount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(EMAIL_LOGIN_FAILED));
        return user;
    }

    /** 토큰 재발급 */
    @Transactional
    public TokenDto reissue(TokenDto tokenRequestDto) {

        Claims claims = jwtProvider.parseClaims(tokenRequestDto.getRefreshToken());
        String email = claims.getSubject();

        User user = getAccount(email);

        RefreshToken refreshToken = getValidateRefreshToken(user, tokenRequestDto.getRefreshToken());

        TokenDto newToken = createToken(user);
        refreshToken.updateToken(newToken.getRefreshToken());

        return newToken;
    }


    private RefreshToken getValidateRefreshToken(User user, String requestRefreshToken) {
        RefreshToken refreshToken = getRefreshToken(user);
        validateRefreshToken(refreshToken, requestRefreshToken);
        return refreshToken;
    }

    private void validateRefreshToken(RefreshToken refreshToken, String requestRefreshToken) {
        if (refreshToken == null) {
            throw new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID);
        }
        if (!isValidationRefreshToken(requestRefreshToken)) {
            throw new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID);
        }
        if (!isEqualsRefreshToken(refreshToken, requestRefreshToken)) {
            throw new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID);
        }
    }

    private boolean isValidationRefreshToken(String refreshToken) {
        return jwtProvider.validationToken(refreshToken);
    }

    private boolean isEqualsRefreshToken(RefreshToken refreshToken, String requestRefreshToken) {
        return refreshToken.getToken().equals(requestRefreshToken);
    }
}
