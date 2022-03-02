package com.importH.domain.notification;

import com.importH.domain.post.Post;
import com.importH.domain.user.entity.User;
import com.importH.global.event.PostUpdatedEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository notificationRepository;

    @InjectMocks
    NotificationService notificationService;


    @Test
    @DisplayName("[성공] 알람 생성 정상적인 요청")
    void createNotification_success() throws Exception {
        // given
        PostUpdatedEventDto dto = givenPostUpdatedDto();
        given(notificationRepository.save(any())).willReturn(
                getNotification());

        // when
        Long notification_id = notificationService.createNotification(dto);

        //then
        assertThat(notification_id).isEqualTo(getNotification().getId());

        verify(notificationRepository, times(1)).save(any());

    }

    private Notification getNotification() {
        return Notification.builder()
                .id(100L)
                .notificationType(givenPostUpdatedDto().getNotificationType())
                .title(givenPostUpdatedDto().getMsg())
                .user(givenPostUpdatedDto().getPost().getUser())
                .link(givenPostUpdatedDto().getPost().getId() + "")
                .build();
    }

    private PostUpdatedEventDto givenPostUpdatedDto() {
        Post post = Post.builder()
                .id(101L)
                .title("테스트")
                .content("테스트")
                .user(User.builder().email("테스트").nickname("테스트").build())
                .build();
        return new PostUpdatedEventDto(post, post.getTitle() + "게시글에 댓글이 달렸습니다.");
    }

}