package com.importH.domain.notification;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.NotificationErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class NotificationControllerTest {

    public static final String MESSAGES = "/v1/messages";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationFactory notificationFactory;

    @Autowired
    PostFactory postFactory;

    User user;

    @BeforeEach
    void init() {
         user = userRepository.findByNickname("테스트").orElseGet(() -> null);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 로그인한 유저 모든 알림 가져오기")
    void findAll_Notification_success() throws Exception {

        // given
        int quantity = 10;
        for (int i = 0; i < 10; i++) {
            createNotification(user ,false);
        }

        // when
        ResultActions perform = mockMvc.perform(get(MESSAGES));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*]", hasSize(quantity)))
                .andExpect(jsonPath("$.list[*].id").exists())
                .andExpect(jsonPath("$.list[*].title").exists())
                .andExpect(jsonPath("$.list[*].checked").exists());

    }

    @Test
    @DisplayName("[실패] 로그인한 유저 모든 알림 가져오기 - 로그인한 유저가 아닌경우")
    void findAll_Notification_fail_not_Login() throws Exception {

        // when
        ResultActions perform = mockMvc.perform(get(MESSAGES));

        //then
        perform.andExpect(status().is3xxRedirection())
                .andDo(print());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 알림 읽기 - 정상적인 요청")
    void checkNotification_success() throws Exception {

        // given
        Notification notification = createNotification(user, false);

        // when
        ResultActions perform = mockMvc.perform(get(MESSAGES + "/" + notification.getId()));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(notification.getLink()));

        assertThat(notification.isChecked()).isTrue();
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 알림 읽기 - 권한이 없는 유저")
    void checkNotification_failed_access_Denied() throws Exception {

        // given
        User user1 = userRepository.findByNickname("test0").get();
        Notification notification = createNotification(user1, false);
        SecurityErrorCode err = SecurityErrorCode.ACCESS_DENIED;

        // when
        ResultActions perform = mockMvc.perform(get(MESSAGES + "/" + notification.getId()));

        // then
        perform.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 알림 읽기 - 존재하지 않는 알림")
    void checkNotification_failed_not_exist() throws Exception {

        // given
        NotificationErrorCode err = NotificationErrorCode.NOT_EXIST_NOTIFICATION;

        // when
        ResultActions perform = mockMvc.perform(get(MESSAGES + "/1000"));

        // then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

    }
    private Notification createNotification(User user, boolean checked) {
            return notificationFactory.createNotification(user, checked, NotificationType.POST_UPDATED, "link", "제목");
    }


}