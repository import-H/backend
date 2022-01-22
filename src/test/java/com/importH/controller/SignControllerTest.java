package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.dto.sign.UserLoginRequestDto;
import com.importH.dto.sign.UserSignUpRequestDto;
import com.importH.error.code.UserErrorCode;
import com.importH.repository.UserRepository;
import com.importH.service.sign.SignService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SignControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SignService signService;

    @BeforeEach
    void setup() {
        UserSignUpRequestDto requestDto = UserSignUpRequestDto.builder()
                .email("user@hongik.ac.kr")
                .nickname("test")
                .password("12341234").build();
        signService.signup(requestDto);
    }
    @AfterEach
    void afterAll() {
        userRepository.deleteAll();
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
    }
    @Test
    void 회원가입요청_실패_중복이메일() throws Exception {


        UserErrorCode userErrorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        UserSignUpRequestDto requestDto = getSignUpRequestDto("12341234");

        userRepository.save(requestDto.toEntity("1234"));

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
                .email("test@hongik.ac.kr")
                .nickname("test")
                .password(password).build();
    }

    @Test
    void 로그인_성공() throws Exception {


        UserLoginRequestDto requestDto = getUserLoginRequestDto("user@hongik.ac.kr");

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());

    }

    @Test
    void 로그인_실패() throws Exception {

        UserErrorCode userErrorCode = UserErrorCode.EMAIL_LOGIN_FAILED;
        UserLoginRequestDto requestDto = getUserLoginRequestDto("user1@hongik.ac.kr");

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(userErrorCode.getDescription()));

    }

    private UserLoginRequestDto getUserLoginRequestDto(String email) {
        UserLoginRequestDto requestDto = UserLoginRequestDto.builder()
                .email(email)
                .password("12341234").build();
        return requestDto;
    }

    @Test
    void reissue() {
    }
}