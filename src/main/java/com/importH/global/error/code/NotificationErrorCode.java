package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode{
    NOT_EXIST_NOTIFICATION("해당 알람은 존재하지 않습니다.", 400);

    private final String description;
    private final int status;
}
