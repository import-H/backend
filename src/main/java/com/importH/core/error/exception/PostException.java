package com.importH.core.error.exception;

import com.importH.core.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class PostException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public PostException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public PostException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public PostException() {
        super();
    }
}
