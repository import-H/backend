package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialErrorCode implements ErrorCode{
    SOCIAL_LOGIN_FAILED("이메일 또는 이름이 없습니다.", 400),
    NOT_VALID_ACCESS("유효하지 않은 접근입니다. 옳바른 경로로 접근 해주세요.", 401);
    private final String description;
    private final int status;
}
