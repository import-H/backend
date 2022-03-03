package com.importH.global.error.exception;

import com.importH.global.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class SecurityException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public SecurityException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
