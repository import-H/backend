package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.UserFactory;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.sign.SignupDto;
import com.importH.error.code.CommonErrorCode;
import com.importH.error.code.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class SignControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Test
    @DisplayName("[성공] 회원가입")
    void signup_success() throws Exception {
        // given
        SignupDto dto = getSignupDto("테스트@mail.com", "test1", "테스트");

        // when
        ResultActions perform = mvc.perform(post("/v1/signup")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(userRepository.existsByEmail("테스트@mail.com")).isTrue();

    }

    @Test
    @DisplayName("[실패] 회원가입 - 옳바르지 않은 파라미터 ")
    void signup_fail_01() throws Exception {
        // given
        SignupDto dto = getSignupDto("테스트@mail.com", "test1", "");
        CommonErrorCode err = CommonErrorCode.NOT_VALID_PARAM;
        // when
        ResultActions perform = mvc.perform(post("/v1/signup")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }

    @Test
    @DisplayName("[실패] 회원가입 - 이메일 중복 ")
    void signup_fail_02() throws Exception {
        // given
        userFactory.createNewAccount("테스트", "테스트@mail.com", "test2", true);

        SignupDto dto = getSignupDto("테스트@mail.com", "test1", "닉네임");
        UserErrorCode err = UserErrorCode.USER_EMAIL_DUPLICATED;

        // when
        ResultActions perform = mvc.perform(post("/v1/signup")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }

    @Test
    @DisplayName("[실패] 회원가입 - 닉네임 중복 ")
    void signup_fail_03() throws Exception {
        // given
        userFactory.createNewAccount("테스트", "테스트@mail.com", "test2", true);

        SignupDto dto = getSignupDto("테스트2@mail.com", "test1", "테스트");
        UserErrorCode err = UserErrorCode.USER_NICKNAME_DUPLICATED;

        // when
        ResultActions perform = mvc.perform(post("/v1/signup")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }

    @Test
    @DisplayName("[실패] 회원가입 - 주소 아이디 중복 ")
    void signup_fail_04() throws Exception {
        // given
        userFactory.createNewAccount("테스트", "테스트@mail.com", "test2", true);
        SignupDto dto = getSignupDto("테스트2@mail.com", "test2", "테스트2");
        UserErrorCode err = UserErrorCode.USER_PATH_ID_DUPLICATED;
        // when
        ResultActions perform = mvc.perform(post("/v1/signup")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }

    private SignupDto getSignupDto(String email, String pathId, String nickname) {
        SignupDto dto = SignupDto.builder()
                .email(email)
                .pathId(pathId)
                .nickname(nickname)
                .password("12341234")
                .confirmPassword("12341234")
                .agree(true)
                .build();
        return dto;
    }


}