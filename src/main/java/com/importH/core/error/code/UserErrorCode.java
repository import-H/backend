package com.importH.core.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode{
    USER_EMAIL_DUPLICATED("해당 이메일은 사용중입니다.", 400),
    USER_NICKNAME_DUPLICATED("해당 닉네임은 사용중입니다.", 400),
    USER_PASSWORD_CHECK("비밀번호와 확인이 동일하지 않습니다.", 400),
    NOT_FOUND_USERID("해당 유저아이디는 없는 회원입니다.", 400),
    ALREADY_USER_DELETED("해당 아이디는 이미 삭제 처리 되었습니다. ", 400),
    EMAIL_LOGIN_FAILED("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.", 400),
    NOT_VALID_REQUEST_PARAMETERS("잘못된 파라미터 요청입니다.", 400);

    private final String description;
    private final int status;
}
