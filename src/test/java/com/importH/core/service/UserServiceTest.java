package com.importH.core.service;

import com.importH.core.AccountFactory;
import com.importH.core.WithAccount;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.user.UserDto;
import com.importH.core.dto.user.UserDto.Response;
import com.importH.core.error.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class UserServiceTest {


    @Autowired
    UserService userService;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    UserRepository userRepository;

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 프로필 정보 가져오기")
    void getUserInfo_success() throws Exception {
        // given
        User account = userRepository.findByNickname("테스트").get();

        // when
        Response user = userService.findUserById(account.getId(), account);

        //then
        assertThat(user)
                .hasFieldOrPropertyWithValue("nickname", account.getNickname())
                .hasFieldOrPropertyWithValue("email", account.getEmail())
                .hasFieldOrPropertyWithValue("profileImage", account.getProfileImage())
                .hasFieldOrPropertyWithValue("introduction", account.getIntroduction())
                .hasFieldOrPropertyWithValue("personalUrl", account.getPersonalUrl())
                .hasFieldOrPropertyWithValue("infoByEmail", account.getInfoAgree().isInfoByEmail())
                .hasFieldOrPropertyWithValue("infoByWeb", account.getInfoAgree().isInfoByWeb());

    }

    @Test
    @WithAccount("테스트1")
    @DisplayName("[실패] 프로필 정보 가져오기 - 유저 정보 동일하지 않음")
    void getUserInfo_fail_notAccordUser() throws Exception {
        // given
        User account = accountFactory.createNewAccount("테스트");
        User test = userRepository.findByNickname("테스트1").get();

        // when
        //then
        assertThrows(UserException.class, () -> userService.findUserById(account.getId(), test));
    }

    @Test
    @WithAccount("테스트1")
    @DisplayName("[실패] 프로필 정보 가져오기 - 로그인 하지 않은 유저")
    void getUserInfo_fail_notLogin() throws Exception {
        // when
        User test = userRepository.findByNickname("테스트1").get();
        //then
        assertThrows(UserException.class, () -> userService.findUserById(test.getId(), null));
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 프로필 정보 수정하기 ")
    void updateUser_success() throws Exception {
        // given
        UserDto.Request request = UserDto.Request.builder()
                .nickname("변경")
                .infoByWeb(false)
                .infoByEmail(false)
                .introduction("안녕하세요.")
                .build();

        User user = userRepository.findByNickname("테스트").get();

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
                .hasFieldOrPropertyWithValue("infoByWeb", request.isInfoByWeb());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 프로필 정보 수정하기 - 동일하지 않은 회원 ")
    void updateUser_fail_notAccord_User() throws Exception {

        // given
        UserDto.Request request = UserDto.Request.builder()
                .nickname("변경")
                .infoByWeb(false)
                .infoByEmail(false)
                .introduction("안녕하세요.")
                .build();

        User user = userRepository.findByNickname("테스트").get();
        User another = userRepository.findByNickname("test1").get();

        // when
        //then
        assertThrows(UserException.class, () -> userService.updateUser(another.getId(), user, request));

    }

}