package com.importH.core.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements  ErrorCode {

    NOT_VALIDATE_PARAM("잘못된 요청 입니다.", 400),
    NOT_FOUND_POST("해당 게시글을 찾을 수 없습니다", 400),
    NOT_ACCORD_ACCOUNT("게시글을 수정할 권한이 없습니다.", 400);



    private final String description;
    private final int status;
}
