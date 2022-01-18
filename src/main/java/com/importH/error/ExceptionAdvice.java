package com.importH.error;

import com.importH.error.exception.JwtException;
import com.importH.error.exception.UserException;
import com.importH.model.response.CommonResult;
import com.importH.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult userException(HttpServletRequest request, UserException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());

    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult jwtException(HttpServletRequest request, JwtException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());

    }

}
