package com.importH.error.exception;

import com.importH.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public CommonException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
