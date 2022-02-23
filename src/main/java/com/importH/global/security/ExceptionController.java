package com.importH.global.security;

import com.importH.global.response.CommonResult;
import com.importH.global.error.exception.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static com.importH.global.error.code.JwtErrorCode.ACCESS_DENIED;
import static com.importH.global.error.code.JwtErrorCode.AUTHENTICATION_ENTRYPOINT;

@ApiIgnore
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
