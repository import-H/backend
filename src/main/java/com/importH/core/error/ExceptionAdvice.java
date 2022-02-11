package com.importH.core.error;

import com.importH.core.error.code.BannerErrorCode;
import com.importH.core.error.exception.*;
import com.importH.core.model.response.CommonResult;
import com.importH.core.service.response.ResponseService;
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
        log.error("requestUrl : {} , errorCode : {}, errorMessage : {}", request.getRequestURI(), e.getErrorCode(), e.getErrorMessage());

        return responseService.getFailResult(e.getErrorCode());

    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult jwtException(HttpServletRequest request, JwtException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }

    @ExceptionHandler(PostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult postException(HttpServletRequest request, PostException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult FileException(HttpServletRequest request, FileException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }


    @ExceptionHandler(CommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult CommentException(HttpServletRequest request, CommentException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }

    @ExceptionHandler(BannerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult BannerException(HttpServletRequest request, BannerException e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }

}
