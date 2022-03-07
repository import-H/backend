package com.importH.global.event;


import com.importH.domain.notification.NotificationService;
import com.importH.domain.post.entity.Post;
import com.importH.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class GlobalEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handlerPostUpdatedEvent(PostUpdatedEventDto postUpdatedEventDto) {

        Post post = postUpdatedEventDto.getPost();
        User user = post.getUser();

        if (user.isInfoByWeb()) {
            notificationService.createNotification(postUpdatedEventDto);
        }

    }
}
