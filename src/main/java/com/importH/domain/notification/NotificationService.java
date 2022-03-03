package com.importH.domain.notification;

import com.importH.domain.notification.NotificationDto.Response;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.exception.NotificationException;
import com.importH.global.error.exception.SecurityException;
import com.importH.global.event.PostUpdatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.importH.global.error.code.NotificationErrorCode.NOT_EXIST_NOTIFICATION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Long createNotification(PostUpdatedEventDto postUpdatedEventDto) {

        Notification notification = Notification.create(postUpdatedEventDto);

        return notificationRepository.save(notification).getId();
    }

    public List<Response> findAll(User user) {


        List<Notification> notifications = notificationRepository.findAllByUser(user);

        return notifications.stream().map(notification -> Response.FromEntity(notification))
                .collect(Collectors.toList());
    }

    @Transactional
    public String checkNotification(User user, Long messageId) {

        Notification notification = notificationRepository.findById(messageId).orElseThrow(() -> new NotificationException(NOT_EXIST_NOTIFICATION));

        if (!notification.getUser().equals(user)) {
            throw new SecurityException(SecurityErrorCode.ACCESS_DENIED);
        }

        notification.checked();
        return notification.getLink();
    }
}
