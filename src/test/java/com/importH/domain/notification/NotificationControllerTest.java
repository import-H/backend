package com.importH.domain.notification;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 로그인한 유저 모든 알림 가져오기")
    void findAll_Notification_success() throws Exception {

        // given
        User user = userRepository.findByNickname("테스트").get();
        int quantity = 10;
        createNotification(user, quantity,false);

        // when
        ResultActions perform = mockMvc.perform(get(MESSAGES));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*]", hasSize(quantity)))
                .andExpect(jsonPath("$.list[*].id").exists())
                .andExpect(jsonPath("$.list[*].link").exists())
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

    private void createNotification(User user, int quantity, boolean checked) {
        for (int i = 0; i < quantity; i++) {
            notificationFactory.createNotification(user, checked, NotificationType.POST_UPDATED, "link"+i, "제목"+i);
        }
    }


}