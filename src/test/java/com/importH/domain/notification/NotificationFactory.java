package com.importH.domain.notification;

import com.importH.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class NotificationFactory {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(User user, boolean checked, NotificationType notificationType, String link, String title) {
        return  notificationRepository.save(
                Notification.builder()
                        .user(user)
                        .notificationType(notificationType)
                        .link(link)
                        .title(title)
                        .checked(checked)
                        .build()
        );
    }

}
