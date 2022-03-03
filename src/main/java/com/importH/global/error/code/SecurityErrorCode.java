package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements ErrorCode{
    AUTHENTICATION_ENTRYPOINT("해당 리소스에 접근하기 위한 권한이 없습니다.", 401),
    ACCESS_DENIED("해당 리소스에 접근할 수 없는 권한입니다.", 403),
    REFRESH_TOKEN_VALID("리프레쉬 토큰이 잘못되었습니다.",400),
    EXPIRED_ACCESS_TOKEN("액세스 토큰이 만료되었습니다.",400);


    private final String description;
    private final int status;
}
