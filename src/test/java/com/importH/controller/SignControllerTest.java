package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.dto.sign.LoginDto;
import com.importH.core.dto.sign.UserSignUpRequestDto;
import com.importH.core.domain.account.Account;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.domain.token.RefreshTokenRepository;
import com.importH.core.domain.account.AccountRepository;
import com.importH.core.service.SignService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:/application-test.properties")
class SignControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SignService signService;

    @Autowired
    RefreshTokenRepository tokenRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    Account account;


    @BeforeEach
    void setup() {
        UserSignUpRequestDto requestDto = UserSignUpRequestDto.builder()
                .email("user@hongik.ac.kr")
                .nickname("test1")
                .password("12341234")
                .confirmPassword("12341234")
                .build();
        signService.signup(requestDto);

        account = accountRepository.findByEmail(requestDto.getEmail()).get();
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    @AfterEach
    void afterAll() {
        accountRepository.deleteAll();
    }

    @Test
    void 회원가입요청_성공() throws Exception {

        UserSignUpRequestDto requestDto = getSignUpRequestDto("12341234");

        mockMvc.perform(post("/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        Optional<Account> account = accountRepository.findByEmail(requestDto.getEmail());
        assertThat(account).isNotNull();
        assertThat(tokenRepository.findByKey(account.get().getId())).isNotNull();
    }

    @Test
    void 회원가입요청_실패_조건만족_X() throws Exception {

        UserSignUpRequestDto requestDto = getSignUpRequestDto("1234");

        mockMvc.perform(post("/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").exists());

        assertThat(accountRepository.findByEmail(requestDto.getEmail())).isEmpty();
    }

    @Test
    void 회원가입요청_실패_중복이메일() throws Exception {


        UserErrorCode userErrorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        UserSignUpRequestDto requestDto = getSignUpRequestDto("12341234");

        accountRepository.save(requestDto.toEntity());

        assertThat(accountRepository.findByEmail(requestDto.getEmail())).isNotNull();

        mockMvc.perform(post("/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(userErrorCode.getDescription()));
    }

    private UserSignUpRequestDto getSignUpRequestDto(String password) {
        return UserSignUpRequestDto.builder()
                .email("test1@hongik.ac.kr")
                .nickname("test12")
                .password(password)
                .confirmPassword(password)
                .agree(true)
                .build();
    }

    @Test
    void 로그인_성공() throws Exception {


        LoginDto.Request requestDto = getUserLoginRequestDto("user@hongik.ac.kr", "12341234");

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());

        assertThat(tokenRepository.findByKey(account.getId()).get().getToken()).isNotNull();


    }

    @Test
    @DisplayName("[실패] 로그인 요청 - 이메일 오류")
    void 로그인_실패() throws Exception {

        UserErrorCode userErrorCode = UserErrorCode.EMAIL_LOGIN_FAILED;
        LoginDto.Request requestDto = getUserLoginRequestDto("user1@hongik.ac.kr", "12341234");

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(userErrorCode.getDescription()));

        assertThat(accountRepository.findByEmail(requestDto.getEmail())).isEmpty();
    }


    @Test
    @DisplayName("[실패] 로그인 요청 - 비밀번호 오류")
    void 로그인_실패_비밀번호() throws Exception {

        UserErrorCode userErrorCode = UserErrorCode.EMAIL_LOGIN_FAILED;
        LoginDto.Request requestDto = getUserLoginRequestDto("user@hongik.ac.kr", "123123");
        Optional<Account> account = accountRepository.findByEmail(requestDto.getEmail());

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(userErrorCode.getDescription()));

        assertThat(tokenRepository.findByKey(account.get().getId())).isEmpty();
    }

    private LoginDto.Request getUserLoginRequestDto(String email, String password) {
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email(email)
                .password(password).build();
        return requestDto;
    }


    @Test
    @DisplayName("[성공] 토큰 재발급 정상 요청")
    void 토큰재발급_성공() throws Exception {

        // given
        LoginDto.Response response = signService.login("user@hongik.ac.kr","12341234");

        // when
        ResultActions perform = mockMvc.perform(post("/v1/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)));

        TokenDto reissue = signService.reissue(TokenDto.builder()
                        .accessToken(response.getAccessToken())
                        .refreshToken(response.getRefreshToken())
                .build());
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(reissue.getAccessToken()))
                .andExpect(jsonPath("$.data.refreshToken").value(reissue.getRefreshToken()));

    }

    @Test
    @DisplayName("[실패] 토큰 재발급 - 잘못된 리프레시 토큰")
    void 토큰재발급_실패 () throws Exception {
        // given
        LoginDto.Response login = signService.login("user@hongik.ac.kr", "12341234");
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(login.getAccessToken())
                .refreshToken(login.getRefreshToken()).build();
        tokenDto.setRefreshToken(createRefreshToken(account.getEmail(),account.getRoles(),30 * 1000L));

        assertThat(tokenRepository.findByKey(account.getId())).isNotNull();
        assertThat(tokenRepository.existsByToken(tokenDto.getRefreshToken())).isFalse();
        JwtErrorCode errorCode = JwtErrorCode.REFRESH_TOKEN_VALID;

        // when
        ResultActions perform = mockMvc.perform(post("/v1/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenDto)));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()))
                .andDo(print());
    }



    private String  createRefreshToken(String email, List<String> roles, Long refreshTokenValidTime) {

        Claims claims = Jwts.claims().setSubject(email); // JWT PALYLOAD 에 저장되는 정보단위
        claims.put("roles", roles);

        Date now = new Date();

        return  Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}