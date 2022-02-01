package com.importH.core.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements  ErrorCode {

    POST_NOT_VALIDATE("잘못된 요청 입니다.", 400),
    POST_NOT_FOUND("해당 게시글을 찾을 수 없습니다", 400);


    private final String description;
    private final int status;
}
