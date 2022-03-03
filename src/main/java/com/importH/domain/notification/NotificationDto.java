package com.importH.domain.notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel("알림 DTO")
public class NotificationDto {

    @Getter
    @Builder
    public static class ResponseAll {

        @ApiModelProperty(value = "알림 id", example = "1")
        private Long id;

        @ApiModelProperty(value = "알림 제목", example = "...게시글에 댓글이 달렸습니다.")
        private String title;

        @ApiModelProperty(value = "생성 시간")
        private LocalDateTime createdAt;


        @ApiModelProperty(value = "알림 확인 여부", example = "true")
        private boolean checked;

        public static ResponseAll FromEntity(Notification notification) {
            return ResponseAll.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .createdAt(notification.getCreatedAt())
                    .checked(notification.isChecked())
                    .build();
        }
    }


}
