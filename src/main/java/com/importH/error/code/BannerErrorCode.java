package com.importH.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BannerErrorCode implements  ErrorCode {

    NOT_AUTHORITY_ACCESS("접근할  권한이 없습니다.", 403),
    NOT_FOUND_BANNER("해당 배너를 찾을수 없습니다.",400);


    private final String description;
    private final int status;
}
