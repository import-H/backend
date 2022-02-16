package com.importH.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode{
    NOT_FOUND("해당 댓글을 찾을 수 없습니다.", 400),
    NOT_EQUALS_USER("수정할 권한이 없습니다.", 403),
    NOT_EQUALS_POST("게시글이 동일하지 않습니다.",400);


    private final String description;
    private final int status;
}
