package com.importH.controller.exception;

import com.importH.error.code.UserErrorCode;
import com.importH.error.exception.JwtException;
import com.importH.error.exception.UserException;
import com.importH.model.response.CommonResult;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.importH.error.code.JwtErrorCode.ACCESS_DENIED;
import static com.importH.error.code.JwtErrorCode.AUTHENTICATION_ENTRYPOINT;

@Api(tags = {"3. Exception"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @GetMapping("/entryPoint")
    public CommonResult entrypointException() {
        throw new JwtException(AUTHENTICATION_ENTRYPOINT);
    }

    @GetMapping("/accessDenied")
    public CommonResult accessDeniedException() {
        throw new JwtException(ACCESS_DENIED);
    }
}
