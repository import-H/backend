package com.importH.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BannerErrorCode implements  ErrorCode {

    NOT_FOUND_BANNER("해당 배너를 찾을수 없습니다.",400);


    private final String description;
    private final int status;
}
