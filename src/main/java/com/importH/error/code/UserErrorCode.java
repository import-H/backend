package com.importH.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode{
    USER_EMAIL_DUPLICATED("해당 이메일은 사용중입니다.", 400),
    USER_NICKNAME_DUPLICATED("해당 닉네임은 사용중입니다.", 400),
    NOT_PASSWORD_EQUALS("입력된 비밀번호와 비밀번호 확인이 동일하지 않습니다.", 400),
    NOT_FOUND_USERID("해당 유저아이디는 없는 회원입니다.", 400),
    NOT_EQUALS_USER("동일한 회원이 아닙니다", 400),
    NOT_VERIFIED_EMAIL("이메일 인증이 되지 않은 유저 입니다.", 403),
    ALREADY_USER_DELETED("해당 아이디는 이미 삭제 처리 되었습니다. ", 400),
    EMAIL_LOGIN_FAILED("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.", 400),
    NOT_FOUND_USER_BY_EMAIL("요청한 이메일에 해당하는 계정이 없습니다.",400),
    USER_PATH_ID_DUPLICATED("해당 주소 id 값은 사용중입니다.", 400),
    NOT_EQUALS_EMAIL_TOKEN("이메일 인증 토큰이 동일하지 않습니다.", 400),
    NOT_PASSED_HOUR("이메일 인증을 보낸지 1시간이 지나지 않았습니다.", 400);

    private final String description;
    private final int status;
}
