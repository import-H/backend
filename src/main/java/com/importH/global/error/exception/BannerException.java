package com.importH.global.error.exception;

import com.importH.global.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class BannerException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public BannerException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BannerException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public BannerException() {
        super();
    }
}
