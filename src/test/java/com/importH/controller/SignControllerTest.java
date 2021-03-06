package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.UserFactory;
import com.importH.domain.user.dto.LoginDto;
import com.importH.domain.user.dto.SignupDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.service.SignService;
import com.importH.domain.user.social.OauthAdapterImpl;
import com.importH.domain.user.social.OauthTokenResponse;
import com.importH.domain.user.social.SocialProfile;
import com.importH.domain.user.token.RefreshToken;
import com.importH.domain.user.token.RefreshTokenRepository;
import com.importH.global.error.code.CommonErrorCode;
import com.importH.global.error.code.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class SignControllerTest {

    public static final String V_1_LOGIN = "/v1/login";
    @Autowired
    private MockMvc mvc;

    @MockBean
    OauthAdapterImpl oauthAdapter;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    SignService signService;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserFactory userFactory;




    @Test
    @DisplayName("[??????] ????????????")
    void signup_success() throws Exception {
        // given
        SignupDto dto = getSignupDto("?????????@mail.com", "test0000", "?????????");

        // when
        ResultActions perform = mvc.perform(post("/v1/signup")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(userRepository.existsByEmail("?????????@mail.com")).isTrue();

    }

    @Test
    @DisplayName("[??????] ???????????? - ???????????? ?????? ???????????? ")
    void signup_fail_01() throws Exception {
        // given
        SignupDto dto = getSignupDto("?????????@mail.com", "test1", "");
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
    @DisplayName("[??????] ???????????? - ????????? ?????? ")
    void signup_fail_02() throws Exception {
        // given
        userFactory.createNewAccount("?????????", "?????????@mail.com", "test2", true);

        SignupDto dto = getSignupDto("?????????@mail.com", "test1", "?????????");
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
    @DisplayName("[??????] ???????????? - ????????? ?????? ")
    void signup_fail_03() throws Exception {
        // given
        userFactory.createNewAccount("?????????", "?????????@mail.com", "test2", true);

        SignupDto dto = getSignupDto("?????????2@mail.com", "test1", "?????????");
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
    @DisplayName("[??????] ???????????? - ?????? ????????? ?????? ")
    void signup_fail_04() throws Exception {
        // given
        userFactory.createNewAccount("?????????", "?????????@mail.com", "test2", true);
        SignupDto dto = getSignupDto("?????????2@mail.com", "test2", "?????????2");
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

    public static final String OAUTH_URL = "/v1/social/";

    @Test
    @DisplayName("[??????] ?????? ?????????")
    void socialLogin_success() throws Exception {

        // given
        String provider = "google";
        given(oauthAdapter.getToken(any(),any())).willReturn(OauthTokenResponse.builder().build());
        given(oauthAdapter.getUserProfile(any(), any(), any())).willReturn(socialProfile("?????????", "?????????@mail.com"));


        // when
        ResultActions perform = mvc.perform(get(OAUTH_URL + provider)
                .param("code", "code"));


        //then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.new").value(true));

    }

    @Test
    @DisplayName("[??????] ?????? ????????? - ?????? ?????? ????????? ?????? ???????????? ?????????")
    void socialLogin_success_existUser() throws Exception {

        // given
        String provider = "google";
        User user = userRepository.findByNickname("test1").get();

        given(oauthAdapter.getToken(any(),any())).willReturn(OauthTokenResponse.builder().build());
        given(oauthAdapter.getUserProfile(any(), any(), any())).willReturn(socialProfile("?????????", user.getEmail()));


        // when
        ResultActions perform = mvc.perform(get(OAUTH_URL + provider)
                .param("code", "code"));


        //then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.new").value(false));

    }

    @Test
    @DisplayName("[??????] ?????? ????????? - ????????? ????????? ????????? , ?????? ???????????? ??????")
    void socialLogin_fail() throws Exception {

        // given
        String provider = "google";
        given(oauthAdapter.getToken(any(),any())).willReturn(OauthTokenResponse.builder().build());
        given(oauthAdapter.getUserProfile(any(), any(), any())).willReturn(socialProfile(null, null));

        // when
        ResultActions perform = mvc.perform(get(OAUTH_URL + provider)
                .param("code", "code"));

        //then
        perform
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }


    @Test
    @DisplayName("[??????] ????????? - ???????????? ?????? ")
    void login_success() throws Exception {
        // given
        User user = getNewUser(null);
        LoginDto loginDto = getLoginDto(user, "12341234");

        // when
        ResultActions perform = mvc.perform(post(V_1_LOGIN)
                .content(mapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());

        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("[??????] ????????? - ???????????? ?????? ????????? ?????? ?????????????????? ??????")
    void login_success_update_refreshToken() throws Exception {
        // given
        User user = getNewUser(RefreshToken.builder().token("token").build());
        LoginDto loginDto = getLoginDto(user, "12341234");

        // when
        ResultActions perform = mvc.perform(post(V_1_LOGIN)
                .content(mapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());

        verify(refreshTokenRepository, never()).save(any());

    }

    @Test
    @DisplayName("[??????] ????????? ?????? - ???????????? ?????? ????????????")
    void login_fail_different_Password() throws Exception {
        // given
        User user = getNewUser(null);
        LoginDto loginDto = getLoginDto(user, "123412345");
        UserErrorCode errorCode = UserErrorCode.EMAIL_LOGIN_FAILED;

        // when
        ResultActions perform = mvc.perform(post(V_1_LOGIN)
                .content(mapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

        verify(refreshTokenRepository, times(0)).save(any());
    }

    private User getNewUser(RefreshToken token) {
        return userFactory.createNewAccount("?????????", "?????????", "?????????", true,false,false,token);
    }

    private LoginDto getLoginDto(User user, String password) {
        return LoginDto.builder().email(user.getEmail()).password(password).build();
    }

    private SocialProfile socialProfile(String name, String email) {
        return SocialProfile.builder().name(name).email(email).oauthId("..").imageUrl("...").build();
    }


}