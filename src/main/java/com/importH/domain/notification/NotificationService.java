package com.importH.domain.notification;

import com.importH.global.event.PostUpdatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    public static final String POSTS_URI = "/v1/posts/";
    private final NotificationRepository notificationRepository;


    @Transactional
    public Long createNotification(PostUpdatedEventDto postUpdatedEventDto) {

        Notification notification = Notification.create(postUpdatedEventDto);

        return notificationRepository.save(notification).getId();
    }
}
