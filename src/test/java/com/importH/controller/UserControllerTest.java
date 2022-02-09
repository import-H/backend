package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.WithAccount;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.service.UserService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 정보 조회")
    void getUserInfo_success() throws Exception {
        // given
        User user = userRepository.findByNickname("테스트").get();

        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/" + user.getId()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andExpect(jsonPath("$.data.profileImage").value(user.getProfileImage()))
                .andExpect(jsonPath("$.data.introduction").value(user.getIntroduction()))
                .andExpect(jsonPath("$.data.personalUrl").value(user.getPersonalUrl()))
                .andExpect(jsonPath("$.data.infoByEmail").value(user.getInfoAgree().isInfoByEmail()))
                .andExpect(jsonPath("$.data.infoByWeb").value(user.getInfoAgree().isInfoByWeb()));
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 정보 조회 - 동일하지 않은 회원")
    void getUserInfo_fail() throws Exception {
        // given
        User another = userRepository.findByNickname("test1").get();
        UserErrorCode errorCode = UserErrorCode.NOT_ACCORD_USERID;

        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/" + another.getId()));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 정보 조회 - 없는 회원 ID")
    void getUserInfo_fail_noUser() throws Exception {
        // given
        UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;

        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/9999"));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));
    }


}