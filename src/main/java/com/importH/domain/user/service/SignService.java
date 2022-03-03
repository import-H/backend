package com.importH.domain.user.service;

import com.importH.domain.user.CustomUser;
import com.importH.domain.user.dto.EmailDto;
import com.importH.domain.user.dto.SignupDto;
import com.importH.domain.user.entity.InfoAgree;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.token.RefreshToken;
import com.importH.domain.user.token.RefreshTokenRepository;
import com.importH.domain.user.token.TokenDto;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.code.UserErrorCode;
import com.importH.global.error.exception.SecurityException;
import com.importH.global.error.exception.UserException;
import com.importH.global.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static com.importH.global.error.code.UserErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository tokenRepository;
    private final EmailService emailService;

    @PostConstruct
    public void init() {
        initAccount();
    }

    private void initAccount() {
        userRepository.save(User.builder().nickname("test").email("p2062199@gmail.com").password(passwordEncoder.encode("12341234")).role("ROLE_USER").oauthId("100").emailCheckToken("5fde96c1-46d0-464b-84e3-478170402815").infoAgree(new InfoAgree(true,true)).build());
        userRepository.save(User.builder().nickname("관리자").email("관리자").password(passwordEncoder.encode("1234")).role("ROLE_ADMIN").emailVerified(true).build());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        return new CustomUser(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
    }

    /** 회원가입 */
    @Transactional
    public Long signup(SignupDto userSignupDto) {

        validateSignup(userSignupDto);

        User user = userSignupDto.toEntity();

        String encodePassword = passwordEncoder.encode(userSignupDto.getPassword());
        user.setPassword(encodePassword);

        sendConfirmEmail(user);

        return saveUser(user).getId();
    }

    private void sendConfirmEmail(User user) {
        user.generateEmailToken();
        EmailDto emailDto = createEmailDto(user);
        emailService.sendEmail(emailDto);
    }

    private EmailDto createEmailDto(User user) {
        return EmailDto.builder()
                .subject("Import-H 이메일 인증")
                .nickname(user.getNickname())
                .message("Import - H 이메일 인증을 위해 아래의 링크를 클릭하세요")
                .link("/v1/email-token?token=" + user.getEmailCheckToken() + "&email=" + user.getEmail())
                .linkName("이메일 인증하기")
                .email(user.getEmail())
                .build();
    }


    private void validateSignup(SignupDto userSignupDto) {
        passwordCheck(userSignupDto.getPassword(), userSignupDto.getConfirmPassword());
        duplicatedEmail(userSignupDto.getEmail());
        duplicatedNickname(userSignupDto.getNickname());
        duplicatedPathId(userSignupDto.getPathId());

    }

    private void duplicatedPathId(String pathId) {
        if (userRepository.existsByPathId(pathId)) {
            throw new UserException(USER_PATH_ID_DUPLICATED);
        }
    }

    private void passwordCheck(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new UserException(NOT_PASSWORD_EQUALS);
        }
    }

    private void duplicatedNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(USER_NICKNAME_DUPLICATED);
        }
    }

    private void duplicatedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(USER_EMAIL_DUPLICATED);
        }
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }


    /** 로그인 */
    @Transactional
    public TokenDto login(String email, String password) {
        User user = findUserByEmail(email, EMAIL_LOGIN_FAILED);
        validatePassword(password, user);

        TokenDto tokenDto = createToken(user);
        RefreshToken refreshToken = getRefreshToken(user);

        saveRefreshToken(user, tokenDto, refreshToken);

        return tokenDto;
    }

    private void saveRefreshToken(User user, TokenDto tokenDto, RefreshToken refreshToken) {
        if (refreshToken == null) {
            RefreshToken newRefreshToken = RefreshToken.create(user, tokenDto.getRefreshToken());
            saveRefreshToken(newRefreshToken);
        } else {
            refreshToken.updateToken(tokenDto.getRefreshToken());
        }
    }

    private void saveRefreshToken(RefreshToken newRefreshToken) {
        tokenRepository.save(newRefreshToken);
    }

    private RefreshToken getRefreshToken(User user) {
        return tokenRepository.findByUser(user).orElse(null);
    }

    private TokenDto createToken(User user) {

        return jwtProvider.createToken(user);
    }

    private void validatePassword(String password, User user) {
        if (!isMatchPassword(password, user.getPassword())) {
            throw new UserException(EMAIL_LOGIN_FAILED);
        }
    }

    private boolean isMatchPassword(String password, String accountPassword) {
        return passwordEncoder.matches(password, accountPassword);
    }

    private User findUserByEmail(String email, UserErrorCode errorCode) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(errorCode));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
    }

    /** 토큰 재발급 */
    @Transactional
    public TokenDto reissue(TokenDto tokenRequestDto) {

        Claims claims = jwtProvider.parseClaims(tokenRequestDto.getRefreshToken());
        Long userId = Long.valueOf(claims.getSubject());

        User user = findUserById(userId);

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
        if (refreshToken == null ||
            !isValidationRefreshToken(requestRefreshToken) ||
            !isEqualsRefreshToken(refreshToken, requestRefreshToken)) {

            throw new SecurityException(SecurityErrorCode.REFRESH_TOKEN_VALID);
        }
    }

    private boolean isValidationRefreshToken(String refreshToken) {
        return jwtProvider.validationToken(refreshToken);
    }

    private boolean isEqualsRefreshToken(RefreshToken refreshToken, String requestRefreshToken) {
        return refreshToken.getToken().equals(requestRefreshToken);
    }

    /**
     * 이메일 토큰 인증
     */
    @Transactional
    public void completeSignup(String emailToken, String email) {

        User user = findUserByEmail(email, NOT_FOUND_USER_BY_EMAIL);

        if (!user.isValidToken(emailToken)) {
            throw new UserException(NOT_EQUALS_EMAIL_TOKEN);
        }

        user.completeSignup();
    }


    /**
     * 이메일 다시 보내기
     */
    @Transactional
    public void resendConfirmEmail(String email) {
        User user = findUserByEmail(email, NOT_FOUND_USER_BY_EMAIL);

        if (!user.canSendConfirmEmail()) {
            throw new UserException(NOT_PASSED_HOUR);
        }

        sendConfirmEmail(user);
    }
}
