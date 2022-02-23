package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailErrorCode implements ErrorCode {
    NOT_SENT_EMAIL("이메일을 보내는도중 오류가 발생했습니다.", 500);


    private final String description;
    private final int status;
}
