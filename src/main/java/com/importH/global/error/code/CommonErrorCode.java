package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    NOT_VALID_PARAM("요청 값이 옳바르지 않습니다.", 400);

    private final String description;
    private final int status;
}
