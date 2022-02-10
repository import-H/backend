package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.WithAccount;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.user.UserDto;
import com.importH.core.error.code.ErrorCode;
import com.importH.core.error.code.UserErrorCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 정보 수정")
    void updateUser_success() throws Exception {
        // given
        UserDto.Request request = UserDto.Request.builder()
                .nickname("변경")
                .introduction("소개입니다.")
                .personalUrl("http://cafe.naver.com")
                .build();

        User user = userRepository.findByNickname("테스트").get();

        // when
        ResultActions perform = mockMvc.perform(put("/v1/users/" + user.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value(request.getNickname()))
                .andExpect(jsonPath("$.data.introduction").value(request.getIntroduction()))
                .andExpect(jsonPath("$.data.personalUrl").value(request.getPersonalUrl()))
                .andExpect(jsonPath("$.data.profileImage").value(user.getProfileImage()))
                .andExpect(jsonPath("$.data.infoByEmail").value(user.getInfoAgree().isInfoByWeb()))
                .andExpect(jsonPath("$.data.infoByWeb").value(user.getInfoAgree().isInfoByWeb()));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 정보 수정 - 잘못된 파라미터")
    void updateUser_fail_notValid() throws Exception {
        // given
        UserDto.Request request = UserDto.Request.builder()
                .nickname("1")
                .introduction("소개입니다.")
                .personalUrl("cafe")
                .build();

        User user = userRepository.findByNickname("테스트").get();
        UserErrorCode errorCode = UserErrorCode.NOT_VALID_REQUEST_PARAMETERS;

        // when
        ResultActions perform = mockMvc.perform(put("/v1/users/" + user.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 탈퇴")
    void deleteUser_success() throws Exception {
        // given
        User user = userRepository.findByNickname("테스트").get();

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/users/" + user.getId()));

        //then
        perform.andExpect(jsonPath("$.success").value(true));

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 탈퇴 - 동일하지 않은 회원")
    void deleteUser_fail() throws Exception {
        // given
        User another = userRepository.findByNickname("test1").get();
        UserErrorCode errorCode = UserErrorCode.NOT_ACCORD_USERID;

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/users/" + another.getId()));

        //then
        perform.andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

    }
}