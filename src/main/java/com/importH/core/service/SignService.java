package com.importH.core.service;

import com.importH.config.security.JwtProvider;
import com.importH.core.domain.token.RefreshToken;
import com.importH.core.domain.token.RefreshTokenRepository;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.email.EmailDto;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.dto.jwt.TokenDto.Info;
import com.importH.core.dto.sign.SignupDto;
import com.importH.error.code.JwtErrorCode;
import com.importH.error.exception.JwtException;
import com.importH.error.exception.UserException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static com.importH.error.code.UserErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {


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
        userRepository.save(User.builder().nickname("test").email("abc@hongik.ac.kr").password(passwordEncoder.encode("12341234")).role("ROLE_USER").emailCheckToken("5fde96c1-46d0-464b-84e3-478170402815").build());
        userRepository.save(User.builder().nickname("관리자").email("관리자").password(passwordEncoder.encode("1234")).role("ROLE_ADMIN").emailVerified(true).build());
    }


    /** 회원가입 */
    @Transactional
    public Long signup(SignupDto userSignupDto) {

        validateSignup(userSignupDto);

        User user = userSignupDto.toEntity();

        String encodePassword = passwordEncoder.encode(userSignupDto.getPassword());
        user.setPassword(encodePassword);

        sendEmailConfirmEmail(user);

        return saveUser(user).getId();
    }

    private void sendEmailConfirmEmail(User user) {
        user.generateEmailToken();
        EmailDto emailDto = createEmailDto(user);
        emailService.sendEmail(emailDto);
    }

    private EmailDto createEmailDto(User user) {
        return EmailDto.builder()
                .subject("Import-H 이메일 인증")
                .nickname(user.getNickname())
                .message("Import - H 이메일 인증을 위해 아래의 링크를 클릭하세요")
                .link("/v1/check-email-token?token=" + user.getEmailCheckToken() + "&email=" + user.getEmail())
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
        User user = findUserByEmail(email);
        validatePassword(password, user);
        
        TokenDto tokenDto = createToken(user);
        RefreshToken refreshToken = getRefreshToken(user);

        saveRefreshToken(user, tokenDto, refreshToken);

        return tokenDto;
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
        Info info = Info.fromEntity(user);

        return jwtProvider.createToken(info, user.getRole());
    }

    private void validatePassword(String password, User user) {
        if (!isMatchPassword(password, user.getPassword())) {
            throw new UserException(EMAIL_LOGIN_FAILED);
        }
    }

    private boolean isMatchPassword(String password, String accountPassword) {
        return passwordEncoder.matches(password, accountPassword);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(EMAIL_LOGIN_FAILED));
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

            throw new JwtException(JwtErrorCode.REFRESH_TOKEN_VALID);
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

        User user = findUserByEmail(email);

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
        User user = findUserByEmail(email);

        if (!user.canSendConfirmEmail()) {
            throw new UserException(NOT_PASSED_HOUR);
        }

        sendEmailConfirmEmail(user);
    }

}
