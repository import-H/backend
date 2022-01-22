package com.importH.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.dto.sign.UserSignUpRequestDto;
import com.importH.error.code.UserErrorCode;
import com.importH.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
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

    @AfterEach
    void afterAll() {
        userRepository.deleteAll();
    }

    @Test
    void 회원가입요청_성공() throws Exception {

        UserSignUpRequestDto requestDto = UserSignUpRequestDto.builder()
                .email("test@hongik.ac.kr")
                .nickname("test")
                .password("12341234").build();

        mockMvc.perform(post("/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1))
                .andExpect(jsonPath("$.msg").exists());

    }

    @Test
    void 회원가입요청_실패_조건만족_X() throws Exception {

        UserSignUpRequestDto requestDto = UserSignUpRequestDto.builder()
                .email("test@hongik.ac.kr")
                .nickname("test")
                .password("1234").build();

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
        UserSignUpRequestDto requestDto = UserSignUpRequestDto.builder()
                .email("test@hongik.ac.kr")
                .nickname("test")
                .password("12341234").build();

        userRepository.save(requestDto.toEntity("1234"));

        mockMvc.perform(post("/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(userErrorCode.getDescription()));
    }

    @Test
    void login() {
    }

    @Test
    void reissue() {
    }
}