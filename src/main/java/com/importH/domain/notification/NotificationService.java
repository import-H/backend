package com.importH.domain.notification;

import com.importH.domain.notification.NotificationDto.Response;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.UserErrorCode;
import com.importH.global.error.exception.UserException;
import com.importH.global.event.PostUpdatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    @Transactional
    public Long createNotification(PostUpdatedEventDto postUpdatedEventDto) {

        Notification notification = Notification.create(postUpdatedEventDto);

        return notificationRepository.save(notification).getId();
    }

    public List<Response> findAll(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USERID));

        List<Notification> notifications = notificationRepository.findAllByUser(user);

        return notifications.stream().map(notification -> Response.FromEntity(notification))
                .collect(Collectors.toList());
    }
}
