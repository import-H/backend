package com.importH.core.service;

import com.importH.core.UserFactory;
import com.importH.core.WithAccount;
import com.importH.domain.image.FileService;
import com.importH.domain.user.dto.PasswordDto;
import com.importH.domain.user.dto.SocialDto;
import com.importH.domain.user.dto.UserDto.Request;
import com.importH.domain.user.dto.UserDto.Response;
import com.importH.domain.user.dto.UserDto.Response_findAllUsers;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.service.UserService;
import com.importH.global.error.code.UserErrorCode;
import com.importH.global.error.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class UserServiceTest {


    @Autowired
    UserService userService;

    @MockBean
    FileService fileService;

    @Autowired
    UserFactory userFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    User user;
    @BeforeEach
    void before() {
        Optional<User> optionalUser = userRepository.findByNickname("테스트");

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = null;
        }
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 프로필 정보 가져오기")
    void getUserInfo_success() throws Exception {

        // when
        Response response = userService.findUserById(user.getId(), user);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("nickname", user.getNickname())
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("profileImage", user.getProfileImage())
                .hasFieldOrPropertyWithValue("introduction", user.getIntroduction())
                .hasFieldOrPropertyWithValue("personalUrl", user.getPersonalUrl())
                .hasFieldOrPropertyWithValue("infoByEmail", user.getInfoAgree().isInfoByEmail())
                .hasFieldOrPropertyWithValue("infoByWeb", user.getInfoAgree().isInfoByWeb())
                .hasFieldOrPropertyWithValue("pathId", user.getPathId())
                .hasFieldOrPropertyWithValue("oauthId", user.getOauthId())
                .hasFieldOrPropertyWithValue("emailVerified", user.isEmailVerified());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 프로필 정보 가져오기 - 유저 정보 동일하지 않음")
    void getUserInfo_fail_notAccordUser() throws Exception {
        // given
        User test = userRepository.findByNickname("test1").get();
        UserErrorCode err = UserErrorCode.NOT_EQUALS_USER;
        // when
        UserException exception = assertThrows(UserException.class, () -> userService.findUserById(user.getId(), test));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage",err.getDescription());
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 프로필 정보 수정하기 ")
    void updateUser_success() throws Exception {
        // given
        Request request = getRequest("테스트");

        // when

        Response updateUser = userService.updateUser(user.getId(), user, request);

        //then
        assertThat(updateUser)
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("nickname", request.getNickname())
                .hasFieldOrPropertyWithValue("profileImage", request.getProfileImage())
                .hasFieldOrPropertyWithValue("introduction", request.getIntroduction())
                .hasFieldOrPropertyWithValue("personalUrl", request.getPersonalUrl())
                .hasFieldOrPropertyWithValue("infoByEmail", request.isInfoByEmail())
                .hasFieldOrPropertyWithValue("infoByWeb", request.isInfoByWeb())
                .hasFieldOrPropertyWithValue("oauthId", user.getOauthId())
                .hasFieldOrPropertyWithValue("emailVerified", user.isEmailVerified());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 프로필 정보 수정하기 - 중복되는 닉네임")
    void updateUser_fail_duplicated_nickname() throws Exception {

        // given
        Request request = getRequest("test1");

        UserErrorCode err = UserErrorCode.USER_NICKNAME_DUPLICATED;

        // when
        UserException exception = assertThrows(UserException.class, () -> userService.updateUser(user.getId(), user, request));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage",err.getDescription());
    }

    private Request getRequest(String nickname) {
        Request request = Request.builder()
                .nickname(nickname)
                .infoByWeb(false)
                .infoByEmail(false)
                .introduction("안녕하세요.")
                .build();
        return request;
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 프로필 정보 수정하기 - 동일하지 않은 회원 ")
    void updateUser_fail_notAccord_User() throws Exception {

        // given
        Request request = getRequest("변경");

        User another = userRepository.findByNickname("test1").get();

        // when
        //then
        assertThrows(UserException.class, () -> userService.updateUser(another.getId(), user, request));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 회원 탈퇴")
    void deleteUser_success() throws Exception {

        // when
        userService.deleteUser(user.getId(),user);

        //then
        assertThat(user)
                .hasFieldOrPropertyWithValue("nickname", "삭제된 계정")
                .hasFieldOrPropertyWithValue("password", "deleted"+user.getId())
                .hasFieldOrPropertyWithValue("email", "deleted"+user.getId())
                .hasFieldOrPropertyWithValue("role", null)
                .hasFieldOrPropertyWithValue("deleted", true)
                .hasFieldOrProperty("deletedTime");
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 회원 탈퇴 - 동일한 회원 아닌경우")
    void deleteUser_fail_notAccordUser() throws Exception {
        // given
        User another = userRepository.findByNickname("test1").get();

        // when
        //then
        assertThrows(UserException.class, () -> userService.deleteUser(user.getId(), another));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 패스워드 변경")
    void updatePassword_success() throws Exception {
        // given

        PasswordDto.Request request = getPasswordReq("testtest", "testtest");

        // when
        userService.updatePassword(user.getId(), user, request);

        //then
        assertThat(passwordEncoder.matches(request.getPassword(), user.getPassword())).isTrue();
    }

    private PasswordDto.Request getPasswordReq(String password, String confirmPassword) {
        return PasswordDto.Request.builder().password(password).confirmPassword(confirmPassword).build();
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 패스워드 변경 - 동일하지 않은 유저")
    void updatePassword_fail1() throws Exception {
        // given

        PasswordDto.Request request = getPasswordReq("testtest", "testtest");
        User another = userRepository.findByNickname("test1").get();
        UserErrorCode err = UserErrorCode.NOT_EQUALS_USER;
        // when
        UserException exception = assertThrows(UserException.class, () -> userService.updatePassword(user.getId(), another, request));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage", err.getDescription());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 패스워드 변경 - 패스워드 와 패스워드 확인이 동일하지 않음")
    void updatePassword_fail2() throws Exception {
        // given

        PasswordDto.Request request = getPasswordReq("testtest", "testtest1");
        UserErrorCode err = UserErrorCode.NOT_PASSWORD_EQUALS;
        // when
        UserException exception = assertThrows(UserException.class, () -> userService.updatePassword(user.getId(), user, request));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage", err.getDescription());

    }

    @Test
    @DisplayName("[성공] 유저 정보 가져오기 - 생성 날짜 최신순")
    void findAllUsers_By_Created_Success_01() throws Exception {

        // given
        for (int i = 0; i < 20; i++) {
            String test02 = "test02" + i;
            userFactory.createNewAccount(test02, test02, test02, true);
        }

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        List<Response_findAllUsers> users = userService.findAllUsers(pageRequest);

        //then
        assertThat(users.size()).isEqualTo(10);
        users.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("userId")
                .hasFieldOrProperty("pathId")
                .hasFieldOrProperty("nickname")
                .hasFieldOrProperty("profileImage"));
    }

    @Test
    @DisplayName("[성공] 유저 정보 가져오기 - 이메일 인증 여부에 따른 회원수")
    void findAllUsers_By_Created_Success_02() throws Exception {

        // given
        for (int i = 0; i < 5; i++) {
            String test02 = "test02" + i;
            userFactory.createNewAccount(test02, test02, test02, false);
        }
        for (int i = 0; i < 5; i++) {
            String test03 = "test03" + i;
            userFactory.createNewAccount(test03, test03, test03, true);
        }

        PageRequest pageRequest = PageRequest.of(0, 5);

        // when
        List<Response_findAllUsers> users = userService.findAllUsers(pageRequest);

        //then
        assertThat(users.size()).isEqualTo(5);
        users.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("userId")
                .hasFieldOrProperty("pathId")
                .hasFieldOrProperty("nickname")
                .hasFieldOrProperty("profileImage"));
    }


    @Test
    @WithAccount("소셜로그인")
    @DisplayName("[성공] 유저 게시판 id 생성 - 소셜 로그인 유저 이면서 게시판 아이디가 없는 경우")
    void createPathId_success() throws Exception {
        // given
        SocialDto socialDto = getSocialDto("social1");
        User user = userRepository.findByNickname("소셜로그인").get();

        // when
        userService.createPathId(user.getId(),socialDto);

        //then
        assertThat(user)
                .hasFieldOrPropertyWithValue("pathId", socialDto.getPathId());
    }

    @Test
    @WithAccount("소셜로그인")
    @DisplayName("[성공] 유저 게시판 id 생성 - 해당 게시판 id 가 이미 존재하는경우")
    void createPathId_fail_Duplicated_Path_Id() throws Exception {
        // given
        SocialDto socialDto = getSocialDto("t0");
        User user = userRepository.findByNickname("소셜로그인").get();
        UserErrorCode errorCode = UserErrorCode.USER_PATH_ID_DUPLICATED;
        // when
        UserException exception = assertThrows(UserException.class, () -> userService.createPathId(user.getId(), socialDto));

        //then
        assertThat(exception)
                .hasMessageContaining(errorCode.getDescription());
    }

    private SocialDto getSocialDto(String pathId) {
        SocialDto socialDto = SocialDto.builder().pathId(pathId).build();
        return socialDto;
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 유저 게시판 id 생성 - 소셜 로그인 유저가 아닌경우")
    void createPathId_fail_notSocialUser() throws Exception {
        // given
        SocialDto socialDto = getSocialDto("social1");
        UserErrorCode err = UserErrorCode.NOT_CREATE_SOCIAL_PATH_ID;

        // when
        UserException exception = assertThrows(UserException.class, () -> userService.createPathId(user.getId(), socialDto));

        //then
        assertThat(exception)
                .hasMessageContaining(err.getDescription());
    }

    @Test
    @WithAccount("소셜로그인")
    @DisplayName("[실패] 유저 게시판 id 생성 - 소셜 유저이나 게시판 아이디가 있는경우")
    void createPathId_fail_havePathId() throws Exception {
        // given
        SocialDto socialDto = getSocialDto("social1");
        User user = userRepository.findByNickname("소셜로그인").get();
        user.setPathId("social");
        UserErrorCode err = UserErrorCode.NOT_CREATE_SOCIAL_PATH_ID;

        // when
        UserException exception = assertThrows(UserException.class, () -> userService.createPathId(user.getId(), socialDto));

        //then
        assertThat(exception)
                .hasMessageContaining(err.getDescription());
    }

}