package com.importH.service.sign;

import com.importH.config.security.JwtProvider;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.dto.sign.UserSignUpRequestDto;
import com.importH.core.domain.account.Account;
import com.importH.core.domain.token.RefreshToken;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.error.exception.JwtException;
import com.importH.core.error.exception.UserException;
import com.importH.core.domain.token.RefreshTokenRepository;
import com.importH.core.domain.account.AccountRepository;
import com.importH.core.service.sign.SignService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class SignServiceTest {

    @Autowired
    AccountRepository accountRepository;

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


    UserSignUpRequestDto requestDto;
    Account user;

    @BeforeEach
    void before() {
        requestDto = getSignUpRequestDto("abc@naver.com", "12341234");
        signService.signup(requestDto);
        user = accountRepository.findByEmail(requestDto.getEmail()).get();
    }

    @AfterEach
    void after() {
        accountRepository.deleteAll();
        tokenRepository.deleteAll();

    }

    @Test
    @DisplayName("[성공] 회원가입 요청")
    void signup_success() throws Exception {
        // given
        requestDto = getSignUpRequestDto("test@naver.com", "21341234");

        // when
        Long id = signService.signup(requestDto);

        //then
        assertThat(accountRepository.existsByEmail(requestDto.getEmail())).isTrue();
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
    @DisplayName("[성공] 로그인 요청")
    void login_success() throws Exception {
        // given
        // when
        TokenDto tokenDto = signService.login(requestDto.getEmail(), requestDto.getPassword());

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

        assertThat(tokenRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("[실패] 로그인 요청 - 이메일 오류")
    void login_fail_email() throws Exception {

        assertThrows(UserException.class, () -> signService.login("abc@email.com", requestDto.getPassword()));

        assertThat(tokenRepository.count()).isEqualTo(0);
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
        TokenDto newTokenDto = jwtProvider.createToken(user.getEmail(), List.of("USER"));
        tokenDto.setRefreshToken(newTokenDto.getRefreshToken());

        JwtErrorCode errorCode = JwtErrorCode.REFRESH_TOKEN_VALID;

        // when
        JwtException exception = assertThrows(JwtException.class, () -> signService.reissue(tokenDto));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    private TokenDto loginUser() {
        return signService.login("abc@naver.com", "12341234");
    }

    private UserSignUpRequestDto getSignUpRequestDto(String email, String password) {
        return UserSignUpRequestDto.builder()
                .email(email)
                .password(password)
                .nickname("test")
                .build();
    }


}