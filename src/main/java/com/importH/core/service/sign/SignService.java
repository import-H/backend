package com.importH.core.service.sign;

import com.importH.config.security.JwtProvider;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.domain.token.RefreshTokenRepository;
import com.importH.core.domain.account.Account;
import com.importH.core.domain.token.RefreshToken;
import com.importH.core.dto.sign.UserSignUpRequestDto;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.exception.JwtException;
import com.importH.core.error.exception.UserException;
import com.importH.core.domain.account.AccountRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.importH.core.error.code.UserErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {


    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository tokenRepository;


    /** 회원가입 */
    @Transactional
    public Long signup(UserSignUpRequestDto userSignUpRequestDto) {
        validateSignup(userSignUpRequestDto);
        String password = passwordEncoder.encode(userSignUpRequestDto.getPassword());
        return saveUser(userSignUpRequestDto.toEntity(password)).getId();
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
        if (accountRepository.existsByNickName(nickname)) {
            throw new UserException(USER_NICKNAME_DUPLICATED);
        }
    }

    private void duplicatedEmail(String email) {
        if (accountRepository.findByEmail(email).orElse(null) != null) {
            throw new UserException(USER_EMAIL_DUPLICATED);
        }
    }

    private Account saveUser(Account account) {
        return accountRepository.save(account);
    }


    /** 로그인 */
    @Transactional
    public TokenDto login(String email, String password) {
        Account account = getAccount(email);
        validatePassword(password,account);
        
        TokenDto tokenDto = createToken(account);
        RefreshToken refreshToken = getRefreshToken(account);

        saveRefreshToken(account, tokenDto, refreshToken);

        return tokenDto;
    }

    private void saveRefreshToken(Account account, TokenDto tokenDto, RefreshToken refreshToken) {
        if (refreshToken == null) {
            RefreshToken newRefreshToken = RefreshToken.create(account.getId(), tokenDto.getRefreshToken());
            saveRefreshToken(newRefreshToken);
        } else {
            refreshToken.updateToken(tokenDto.getRefreshToken());
        }
    }

    private void saveRefreshToken(RefreshToken newRefreshToken) {
        tokenRepository.save(newRefreshToken);
    }

    private RefreshToken getRefreshToken(Account account) {
        return tokenRepository.findByKey(account.getId()).orElse(null);
    }

    private TokenDto createToken(Account account) {
        return jwtProvider.createToken(account.getEmail(), account.getRoles());
    }

    private void validatePassword(String password, Account account) {
        if (!isMatchPassword(password, account.getPassword())) {
            throw new UserException(EMAIL_LOGIN_FAILED);
        }
    }

    private boolean isMatchPassword(String password, String accountPassword) {
        return passwordEncoder.matches(password, accountPassword);
    }

    private Account getAccount(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UserException(EMAIL_LOGIN_FAILED));
        return account;
    }

    /** 토큰 재발급 */
    @Transactional
    public TokenDto reissue(TokenDto tokenRequestDto) {

        Claims claims = jwtProvider.parseClaims(tokenRequestDto.getRefreshToken());
        String email = claims.getSubject();

        Account account = getAccount(email);

        RefreshToken refreshToken = getValidateRefreshToken(account, tokenRequestDto.getRefreshToken());

        TokenDto newToken = createToken(account);
        refreshToken.updateToken(newToken.getRefreshToken());

        return newToken;
    }


    private RefreshToken getValidateRefreshToken(Account account,String requestRefreshToken) {
        RefreshToken refreshToken = getRefreshToken(account);
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
