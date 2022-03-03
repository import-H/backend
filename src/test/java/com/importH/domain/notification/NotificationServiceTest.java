package com.importH.domain.notification;

import com.importH.domain.post.Post;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.NotificationErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.exception.NotificationException;
import com.importH.global.error.exception.SecurityException;
import com.importH.global.event.PostUpdatedEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.importH.global.error.code.NotificationErrorCode.NOT_EXIST_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository notificationRepository;
    @Mock
    UserRepository userRepository;

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

    @Test
    @DisplayName("[성공] 모든 알람 가져오기")
    void findAllNotification_success() throws Exception {

        // given
        given(notificationRepository.findAllByUser(any())).willReturn(List.of(getNotification()));

        // when
        List<NotificationDto.Response> responses = notificationService.findAll(any());

        //then
        assertThat(responses.get(0))
                .hasFieldOrPropertyWithValue("title", getNotification().getTitle())
                .hasFieldOrPropertyWithValue("link", getNotification().getLink())
                .hasFieldOrPropertyWithValue("checked", getNotification().isChecked());

        verify(notificationRepository, times(1)).findAllByUser(any());
    }

    @Test
    @DisplayName("[성공] 알림 읽기 - 정상적인 요청")
    void checkNotification_success() throws Exception {

        // given
        given(notificationRepository.findById(any())).willReturn(Optional.of(getNotification()));

        // when
        String uri = notificationService.checkNotification(getNotification().getUser(), 100L);

        //then
        assertThat(uri).isEqualTo(getNotification().getLink());

        verify(notificationRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("[실패] 알림 읽기 - 권한이 없는 유저")
    void checkNotification_fail_access_Denied() throws Exception {

        // given
        SecurityErrorCode err = SecurityErrorCode.ACCESS_DENIED;
        given(notificationRepository.findById(any())).willReturn(Optional.of(getNotification()));

        // when
        SecurityException exception = assertThrows(SecurityException.class, () -> notificationService.checkNotification(User.builder().build(), 100L));

        //then
        assertThat(exception).hasMessageContaining(err.getDescription());

        verify(notificationRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("[실패] 알림 읽기 - 존재 하지 않는 알람")
    void checkNotification_fail_not_exist() throws Exception {

        // given
        NotificationErrorCode err = NotificationErrorCode.NOT_EXIST_NOTIFICATION;
        given(notificationRepository.findById(any())).willThrow(new NotificationException(NOT_EXIST_NOTIFICATION));

        // when
        NotificationException exception = assertThrows(NotificationException.class, () -> notificationService.checkNotification(User.builder().build(), 100L));

        //then
        assertThat(exception).hasMessageContaining(err.getDescription());

        verify(notificationRepository, times(1)).findById(any());
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
                .user(User.builder().id(300L).email("테스트").nickname("테스트").build())
                .build();
        return new PostUpdatedEventDto(post, post.getTitle() + "게시글에 댓글이 달렸습니다.");
    }

}