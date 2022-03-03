package com.importH.global.error;

import com.importH.global.error.exception.SecurityException;
import com.importH.global.response.ResponseService;
import com.importH.global.response.CommonResult;
import com.importH.global.error.code.ErrorCode;
import com.importH.global.error.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;

    @ExceptionHandler(CommonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult CommonException(HttpServletRequest request, CommonException e) {
        printError(request, e);

        return responseService.getFailResult(e.getErrorCode());

    }

    private void printError(HttpServletRequest request, CommonException e) {
        log.error("requestUrl : {} , errorCode : {}, errorMessage : {}", request.getRequestURI(), e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult userException(HttpServletRequest request, UserException e) {
        printError(request, e.getErrorCode());

        return responseService.getFailResult(e.getErrorCode());

    }

    @ExceptionHandler(NotificationException.class)
    protected ResponseEntity notificationException(HttpServletRequest request, NotificationException e) {
        printError(request, e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(responseService.getFailResult(e.getErrorCode()));
    }

    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity securityException(HttpServletRequest request, SecurityException e) {
        printError(request, e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(responseService.getFailResult(e.getErrorCode()));
    }

    @ExceptionHandler(PostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult postException(HttpServletRequest request, PostException e) {
        printError(request, e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult FileException(HttpServletRequest request, FileException e) {
        printError(request, e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }


    @ExceptionHandler(CommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult CommentException(HttpServletRequest request, CommentException e) {
        printError(request, e.getErrorCode());
        return responseService.getFailResult(e.getErrorCode());
    }

    @ExceptionHandler(BannerException.class)
    protected ResponseEntity BannerException(HttpServletRequest request, BannerException e) {
        printError(request, e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(responseService.getFailResult(e.getErrorCode()));
    }

    @ExceptionHandler(SocialException.class)
    protected ResponseEntity socialException(HttpServletRequest request, SocialException e) {
        printError(request, e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(responseService.getFailResult(e.getErrorCode()));
    }

    private void printError(HttpServletRequest request, ErrorCode e) {
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), e);
    }
    

}
