package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.PostFactory;
import com.importH.core.PostScrapFactory;
import com.importH.core.UserFactory;
import com.importH.core.WithAccount;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.service.PostLikeService;
import com.importH.domain.user.dto.PasswordDto;
import com.importH.domain.user.dto.SocialDto;
import com.importH.domain.user.dto.UserDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.CommonErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.code.UserErrorCode;
import org.junit.jupiter.api.BeforeEach;
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
import static org.hamcrest.Matchers.hasSize;
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

    @Autowired
    UserFactory userFactory;

    User user;

    @BeforeEach
    void init() {
        user = userRepository.findByNickname("테스트").orElse(null);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 정보 조회")
    void getUserInfo_success() throws Exception {
        // given
        user = userRepository.findByNickname("테스트").get();

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
                .andExpect(jsonPath("$.data.infoByWeb").value(user.getInfoAgree().isInfoByWeb()))
                .andExpect(jsonPath("$.data.pathId").value(user.getPathId()))
                .andExpect(jsonPath("$.data.oauthId").value(user.getOauthId()))
                .andExpect(jsonPath("$.data.emailVerified").value(user.isEmailVerified()));
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 정보 조회 - 동일하지 않은 회원")
    void getUserInfo_fail() throws Exception {
        // given
        User another = userRepository.findByNickname("test1").get();
        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

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
                .infoByEmail(true)
                .infoByWeb(true)
                .profileImage("")
                .personalUrl("http://cafe.naver.com")
                .build();

        user = userRepository.findByNickname("테스트").get();

        // when
        ResultActions perform = mockMvc.perform(put("/v1/users/" + user.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value(request.getNickname()))
                .andExpect(jsonPath("$.data.introduction").value(request.getIntroduction()))
                .andExpect(jsonPath("$.data.personalUrl").value(request.getPersonalUrl()))
                .andExpect(jsonPath("$.data.profileImage").value(request.getProfileImage()))
                .andExpect(jsonPath("$.data.infoByEmail").value(request.isInfoByWeb()))
                .andExpect(jsonPath("$.data.infoByWeb").value(request.isInfoByWeb()))
                .andExpect(jsonPath("$.data.emailVerified").value(user.isEmailVerified()));
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

        user = userRepository.findByNickname("테스트").get();
        CommonErrorCode errorCode = CommonErrorCode.NOT_VALID_PARAM;

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
        user = userRepository.findByNickname("테스트").get();

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
        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/users/" + another.getId()));

        //then
        perform.andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 패스워드 변경")
    void updatePassword_success() throws Exception {
        // given
        user = userRepository.findByNickname("테스트").get();
        PasswordDto.Request request = getPasswordReq("testtest", "testtest");

        // when
        ResultActions perform = mockMvc.perform(
                put("/v1/users/" + user.getId() + "/updatePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 패스워드 변경 - 동일하지 않은 유저")
    void updatePassword_fail1() throws Exception {
        // given
        user = userRepository.findByNickname("test1").get();
        PasswordDto.Request request = getPasswordReq("testtest", "testtest");
        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

        // when
        ResultActions perform = mockMvc.perform(
                put("/v1/users/" + user.getId() + "/updatePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 패스워드 변경 - 입력된 비밀번호와 확인이 동일하지 않은 경우")
    void updatePassword_fail2() throws Exception {
        // given
        user = userRepository.findByNickname("테스트").get();
        PasswordDto.Request request = getPasswordReq("testtest", "testtest1");
        UserErrorCode err = UserErrorCode.NOT_PASSWORD_EQUALS;

        // when
        ResultActions perform = mockMvc.perform(
                put("/v1/users/" + user.getId() + "/updatePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));
    }

    @Test
    @DisplayName("[성공] 개인 게시판 모든 유저 정보 조회")
    void findAllUsers_success() throws Exception {
        // given
        for (int i = 0; i < 5; i++) {
            String test02 = "test02" + i;
            userFactory.createNewAccount(test02, test02, test02, false);
        }
        for (int i = 0; i < 5; i++) {
            String test03 = "test03" + i;
            userFactory.createNewAccount(test03, test03, test03, true);
        }

        // when
        ResultActions perform = mockMvc.perform(get("/v1/users"));

        //then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*]", hasSize(Math.toIntExact(userRepository.countByEmailVerified(true)))))
                .andExpect(jsonPath("$.list[*].userId").exists())
                .andExpect(jsonPath("$.list[*].nickname").exists())
                .andExpect(jsonPath("$.list[*].profileImage").exists())
                .andExpect(jsonPath("$.list[*].pathId").exists());
    }


    @Test
    @WithAccount("소셜로그인")
    @DisplayName("[성공] 유저 게시판 id 생성 - 소셜 로그인 유저 이면서 게시판 아이디가 없는 경우")
    void createPathId_success() throws Exception {
        // given
        SocialDto socialDto = getSocialDto();
        user = userRepository.findByNickname("소셜로그인").get();

        // when
        ResultActions perform = mockMvc.perform(put("/v1/users/" + user.getId() + "/path-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(socialDto)));

        //then
        perform
                .andExpect(jsonPath("$.success").value(true));

        assertThat(user.getPathId()).isNotNull();
    }

    @Test
    @WithAccount("소셜로그인")
    @DisplayName("[성공] 유저 게시판 id 생성 - 소셜 로그인 유저 이지만 게시판 아이디가 있는경우")
    void createPathId_fail_exist_PathId() throws Exception {
        // given
        SocialDto socialDto = getSocialDto();
        user = userRepository.findByNickname("소셜로그인").get();
        user.setPathId("social0");
        UserErrorCode errorCode = UserErrorCode.NOT_CREATE_SOCIAL_PATH_ID;

        // when
        ResultActions perform = mockMvc.perform(put("/v1/users/" + user.getId() + "/path-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(socialDto)));

        //then
        perform
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

        assertThat(user.getPathId()).isNotEqualTo(socialDto.getPathId());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 게시판 id 생성 - 소셜 로그인 유저가 아닌경우")
    void createPathId_fail_not_socialLogin() throws Exception {
        // given
        SocialDto socialDto = getSocialDto();
        user = userRepository.findByNickname("테스트").get();
        UserErrorCode errorCode = UserErrorCode.NOT_CREATE_SOCIAL_PATH_ID;

        // when
        ResultActions perform = mockMvc.perform(put("/v1/users/" + user.getId() + "/path-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(socialDto)));

        //then
        perform
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

    }


    @Autowired
    PostFactory postFactory;

    @Autowired
    PostScrapFactory postScrapFactory;

    @Test
    @WithAccount("테스트1")
    @DisplayName("[성공] 유저 스크랩 가져오기")
    void findAllScraps_success() throws Exception {
        // given
        user = userRepository.findByNickname("테스트1").get();
        for (int i = 0; i < 10; i++) {
            Post post = postFactory.createPost(user);
            postScrapFactory.createScrap(user,post);
        }
        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/" + user.getId() + "/scrap"));
        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*].title").exists())
                .andExpect(jsonPath("$.list[*].createdAt").exists())
                .andExpect(jsonPath("$.list[*].author").exists())
                .andExpect(jsonPath("$.list[*].postUri").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(10)));
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 스크랩 가져오기 - 권한이 없는 유저 ")
    void findAllScraps_fail() throws Exception {
        //given
        User another = userRepository.findByNickname("test0").get();
        SecurityErrorCode err = SecurityErrorCode.ACCESS_DENIED;

        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/" + another.getId() + "/scrap"));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }

    @Autowired
    PostLikeService postLikeService;

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 좋아요한 게시글 가져오기")
    void findAllPostByLike_success() throws Exception {
        // given
        user = userRepository.findByNickname("테스트").get();
        for (int i = 0; i < 10; i++) {
            Post post = postFactory.createPost(user);
            postLikeService.addLike(user,post.getId());
        }
        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/" + user.getId() + "/like"));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*].title").exists())
                .andExpect(jsonPath("$.list[*].createdAt").exists())
                .andExpect(jsonPath("$.list[*].author").exists())
                .andExpect(jsonPath("$.list[*].postUri").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(10)));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 유저 작성한 게시글 가져오기")
    void findAllWrotePost_success() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            Post post = postFactory.createPost(user);
        }
        // when
        ResultActions perform = mockMvc.perform(get("/v1/users/" + user.getId() + "/post"));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*].title").exists())
                .andExpect(jsonPath("$.list[*].createdAt").exists())
                .andExpect(jsonPath("$.list[*].author").exists())
                .andExpect(jsonPath("$.list[*].postUri").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(10)));
    }

    private PasswordDto.Request getPasswordReq(String password, String confirmPassword) {
        return PasswordDto.Request.builder().password(password).confirmPassword(confirmPassword).build();
    }

    private SocialDto getSocialDto() {
        SocialDto socialDto = SocialDto.builder().pathId("social1").build();
        return socialDto;
    }

}