package com.importH.error.exception;

import com.importH.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public UserException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public UserException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public UserException() {
        super();
    }
}
