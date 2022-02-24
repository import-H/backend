package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements  ErrorCode {

    NOT_FOUND_POST("해당 게시글을 찾을 수 없습니다", 400),
    NOT_ACCORD_ACCOUNT("게시글을 수정할 권한이 없습니다.", 400),
    NOT_EXIST_TYPE("존재하지 않는 게시판 입니다.", 400);



    private final String description;
    private final int status;
}
