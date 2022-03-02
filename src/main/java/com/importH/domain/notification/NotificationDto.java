package com.importH.domain.notification;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel("알림 DTO")
public class NotificationDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String title;
        private String link;
        private LocalDateTime createdAt;
        private boolean checked;

        public static Response FromEntity(Notification notification) {
            return Response.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .link(notification.getLink())
                    .createdAt(notification.getCreatedAt())
                    .checked(notification.isChecked())
                    .build();
        }

    }
}
