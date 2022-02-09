package com.importH.core.service;

import com.importH.core.AccountFactory;
import com.importH.core.WithAccount;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.user.UserDto;
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
        UserDto.Response user = userService.findUserById(account.getId(), account);

        //then
        assertThat(user)
                .hasFieldOrPropertyWithValue("nickname", account.getNickname());
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

}