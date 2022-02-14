package com.importH.core.service.sign;

import com.importH.config.security.JwtProvider;
import com.importH.core.domain.token.RefreshToken;
import com.importH.core.domain.token.RefreshTokenRepository;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.dto.jwt.TokenDto.Info;
import com.importH.core.dto.sign.SignupDto;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.error.exception.JwtException;
import com.importH.core.error.exception.UserException;
import com.importH.core.service.SignService;
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
        requestDto = getSignUpRequestDto("테스트","abc@naver.com", "12341234");
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
        requestDto = getSignUpRequestDto("test123","test@naver.com", "21341234");

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
        requestDto = getSignUpRequestDto("test1","abc1@naver.com", "12341234");

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
        UserErrorCode errorCode = UserErrorCode.USER_PASSWORD_CHECK;
        requestDto = getSignUpRequestDto("test","abc1@naver.com", "12341234");
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
        RefreshToken refreshToken = tokenRepository.findByKey(user.getId()).get();

        //then
        assertThat(refreshToken.getToken()).isEqualTo(reissue.getRefreshToken());

    }


    @Test
    @DisplayName("[실패] 토큰 재발급 - 옳바르지 않은 토큰")
    void reissue_fail_1() throws Exception {
        // given
        TokenDto tokenDto = loginUser();

        Info info = getTokenClaims();

        TokenDto newTokenDto = jwtProvider.createToken(info, "ADMIN");
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
                .containsEntry("nickname", user.getNickname())
                .containsEntry("profileImage", user.getProfileImage())
                .containsEntry("sub", user.getEmail());

    }

    private Info getTokenClaims() {
        Info info = Info.builder().email(user.getEmail())
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage()).build();
        return info;
    }

    private TokenDto loginUser() {
        TokenDto login = signService.login("abc@naver.com", "12341234");
        return TokenDto.builder().accessToken(login.getAccessToken()).refreshToken(login.getRefreshToken()).build();
    }

    private SignupDto getSignUpRequestDto(String nickname, String email, String password) {
        return SignupDto.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .confirmPassword(password)
                .build();
    }


}