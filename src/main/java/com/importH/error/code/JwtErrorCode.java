package com.importH.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorCode implements ErrorCode{
    AUTHENTICATION_ENTRYPOINT("해당 리소스에 접근하기 위한 권한이 없습니다.", 400),
    ACCESS_DENIED("해당 리소스에 접근할 수 없는 권한입니다.", 400);

    private final String description;
    private final int status;
}
