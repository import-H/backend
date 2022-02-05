package com.importH.core.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FAIL_FILE_SAVE("파일 저장에 실패했습니다.", 400),
    NOT_FOUND_IMAGE("저장할 이미지가 없습니다.", 400);


    private final String description;
    private final int status;
}
