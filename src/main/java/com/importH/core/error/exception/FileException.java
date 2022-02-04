package com.importH.core.error.exception;

import com.importH.core.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class FileException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public FileException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
