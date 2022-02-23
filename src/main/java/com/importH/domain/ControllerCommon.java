package com.importH.domain;

import com.importH.global.error.code.CommonErrorCode;
import com.importH.global.error.exception.CommonException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public interface ControllerCommon {

    static void validParameter(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CommonException(CommonErrorCode.NOT_VALID_PARAM,
                      getErrorMessage(bindingResult.getAllErrors()));
        }
    }
    private static String getErrorMessage(List<ObjectError> errors) {
        return errors.stream()
                .map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.toList())
                .toString();
    }
}
