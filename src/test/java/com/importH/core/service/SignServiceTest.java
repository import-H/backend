package com.importH.core.service;

import com.importH.domain.user.token.RefreshToken;
import com.importH.domain.user.token.RefreshTokenRepository;
import com.importH.domain.user.token.TokenDto;
import com.importH.domain.user.token.TokenDto.Info;
import com.importH.domain.user.service.SignService;
import com.importH.domain.user.dto.SignupDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.JwtErrorCode;
import com.importH.global.error.code.UserErrorCode;
import com.importH.global.error.exception.JwtException;
import com.importH.global.error.exception.UserException;
import com.importH.global.security.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class SignServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Autowired
    RefreshTokenRepository tokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SignService signService;

    @Autowired
    JwtProvider jwtProvider;


    SignupDto requestDto;
    User user;

    @BeforeEach
    void before() {
        requestDto = getSignUpRequestDto("테스트","abc@naver.com", "12341234","test1");
        signService.signup(requestDto);
        user = userRepository.findByEmail(requestDto.getEmail()).get();
    }

    @AfterEach
    void after() {
        userRepository.deleteAll();

    }

    @Test
    @DisplayName("[성공] 회원가입 요청")
    void signup_success() throws Exception {
        // given
        requestDto = getSignUpRequestDto("test123","test@naver.com", "21341234","test2");

        // when
        Long id = signService.signup(requestDto);

        //then
        assertThat(userRepository.existsByEmail(requestDto.getEmail())).isTrue();
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("[실패] 회원가입 요청 - 중복된 메일")
    void signup_fail() throws Exception {

        //given
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;

        //when
        UserException userException = assertThrows(UserException.class, () -> signService.signup(requestDto));

        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    @Test
    @DisplayName("[실패] 회원가입 요청 - 중복된 닉네임")
    void signup_fail_nickname() throws Exception {

        //given
        UserErrorCode errorCode = UserErrorCode.USER_NICKNAME_DUPLICATED;
        requestDto = getSignUpRequestDto("test1","abc1@naver.com", "12341234","test2");

        //when
        UserException userException = assertThrows(UserException.class, () -> signService.signup(requestDto));

        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    @Test
    @DisplayName("[실패] 회원가입 요청 - 중복된 주소 ID")
    void signup_fail_pathId() throws Exception {

        //given
        UserErrorCode errorCode = UserErrorCode.USER_PATH_ID_DUPLICATED;
        requestDto = getSignUpRequestDto("테스트1","abc1@naver.com", "12341234","test1");

        //when
        UserException userException = assertThrows(UserException.class, () -> signService.signup(requestDto));

        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    @Test
    @DisplayName("[실패] 회원가입 요청 - 비밀번호 미일치")
    void signup_fail_password_notValid() throws Exception {

        //given
        UserErrorCode errorCode = UserErrorCode.NOT_PASSWORD_EQUALS;
        requestDto = getSignUpRequestDto("test","abc1@naver.com", "12341234","test2");
        requestDto.setConfirmPassword("1234");
        //when
        UserException userException = assertThrows(UserException.class, () -> signService.signup(requestDto));

        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }


    @Test
    @DisplayName("[성공] 로그인 요청")
    void login_success() throws Exception {
        // given
        // when
        TokenDto login = signService.login(requestDto.getEmail(), requestDto.getPassword());
        TokenDto tokenDto = TokenDto.builder().accessToken(login.getAccessToken()).refreshToken(login.getRefreshToken()).build();

        //then
        assertThat(tokenDto)
                .hasFieldOrProperty("accessToken")
                .hasFieldOrProperty("refreshToken");

        assertThat(tokenRepository.existsByToken(tokenDto.getRefreshToken())).isTrue();
    }

    @Test
    @DisplayName("[실패] 로그인 요청 - 비밀번호 오류")
    void login_fail_password() throws Exception {

        assertThrows(UserException.class, () -> signService.login(requestDto.getEmail(), "1234567"));
    }

    @Test
    @DisplayName("[실패] 로그인 요청 - 이메일 오류")
    void login_fail_email() throws Exception {

        assertThrows(UserException.class, () -> signService.login("abc@email.com", requestDto.getPassword()));
    }


    @Test
    @DisplayName("[성공] 접근 토큰 재발급 정상요청 ")
    void reissue_success() throws Exception {

        // given
        TokenDto tokenDto = loginUser();

        // when
        TokenDto reissue = signService.reissue(tokenDto);
        RefreshToken refreshToken = tokenRepository.findByUser(user).get();

        //then
        assertThat(refreshToken.getToken()).isEqualTo(reissue.getRefreshToken());

    }


    @Test
    @DisplayName("[실패] 토큰 재발급 - 옳바르지 않은 토큰")
    void reissue_fail_1() throws Exception {
        // given
        TokenDto tokenDto = loginUser();
        User admin = userRepository.findByNickname("test1").get();

        TokenDto newTokenDto = jwtProvider.createToken(admin);
        tokenDto.setRefreshToken(newTokenDto.getRefreshToken());

        JwtErrorCode errorCode = JwtErrorCode.REFRESH_TOKEN_VALID;

        // when
        JwtException exception = assertThrows(JwtException.class, () -> signService.reissue(tokenDto));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    @Test
    @DisplayName("토큰 정보 확인")
    void existInfoInToken() throws Exception {
        // given
        TokenDto tokenDto = loginUser();

        // when
        Claims claims = jwtProvider.parseClaims(tokenDto.getAccessToken());

        //then
        assertThat(claims)
                .containsEntry("roles", user.getRole())
                .containsEntry("sub", String.valueOf(user.getId()));

    }

    @Test
    @DisplayName("[성공] 이메일 인증 ")
    void emailVerified_success() throws Exception {

        // when
        signService.completeSignup(user.getEmailCheckToken(), user.getEmail());

        //then
        assertThat(user.isEmailVerified()).isTrue();

    }

    @Test
    @DisplayName("[실패] 이메일 인증 - 잘못된 토큰 ")
    void emailVerified_fail1() throws Exception {

        //given
        UserErrorCode err = UserErrorCode.NOT_EQUALS_EMAIL_TOKEN;

        // when
        UserException exception = assertThrows(UserException.class, () -> signService.completeSignup("", user.getEmail()));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage", err.getDescription());

        assertThat(user.isEmailVerified()).isFalse();

    }

    @Test
    @DisplayName("[실패] 이메일 인증 - 잘못된 이메일 ")
    void emailVerified_fail2() throws Exception {

        //given
        UserErrorCode err = UserErrorCode.NOT_FOUND_USER_BY_EMAIL;

        // when
        UserException exception = assertThrows(UserException.class, () -> signService.completeSignup(user.getEmailCheckToken(), ""));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage", err.getDescription());

        assertThat(user.isEmailVerified()).isFalse();

    }


    @Test
    @DisplayName("[성공] 이메일 인증 다시 보내기 ")
    void resendConfirmEmail_success() throws Exception {
        // given
        user = userRepository.save(User.builder()
                .email("test")
                .password(passwordEncoder.encode("1234"))
                .nickname("test")
                .build());
        String beforeToken = user.getEmailCheckToken();

        // when
        signService.resendConfirmEmail(user.getEmail());

        //then
        assertThat(user.getEmailCheckToken()).isNotEqualTo(beforeToken);
    }

    @Test
    @DisplayName("[실패] 이메일 인증 다시 보내기 - 아직 1시간이 지나지 않은 경우")
    void resendConfirmEmail_fail() throws Exception {
        // given
        String beforeToken = user.getEmailCheckToken();
        UserErrorCode err = UserErrorCode.NOT_PASSED_HOUR;
        // when
        UserException exception = assertThrows(UserException.class, () -> signService.resendConfirmEmail(user.getEmail()));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage", err.getDescription());
    }
    private Info getTokenClaims() {
        Info info = Info.fromEntity(user);
        return info;
    }

    private TokenDto loginUser() {
        TokenDto login = signService.login("abc@naver.com", "12341234");
        return TokenDto.builder().accessToken(login.getAccessToken()).refreshToken(login.getRefreshToken()).build();
    }

    private SignupDto getSignUpRequestDto(String nickname, String email, String password,String pathId) {
        return SignupDto.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .pathId(pathId)
                .confirmPassword(password)
                .build();
    }


}