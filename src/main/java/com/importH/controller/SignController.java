package com.importH.controller;

import com.importH.config.security.JwtProvider;
import com.importH.dto.UserLoginResponseDto;
import com.importH.dto.UserSignUpRequestDto;
import com.importH.error.code.UserErrorCode;
import com.importH.error.exception.UserException;
import com.importH.model.response.SingleResult;
import com.importH.service.UserService;
import com.importH.service.response.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//TODO 컨트롤러 구현
@Api(tags = "1. SignUp / Login")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SignController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation(value = "로그인", notes = "로그인을 합니다.")
    @PostMapping("/login")
    public SingleResult<String> login(
            @ApiParam(value = "로그인 아이디 : 이메일", required = true) @RequestParam String email,
            @ApiParam(value = "로그인 비밀번호", required = true) @RequestParam String password
    ) {
        UserLoginResponseDto userLoginResponseDto = userService.login(email, password);
        String token = jwtProvider.createToken(userLoginResponseDto.getEmail(), userLoginResponseDto.getRoles());
        return responseService.getSingleResult(token);
    }

    @ApiOperation(value = "회원가입 ", notes = "회원가입을 합니다.")
    @PostMapping("/signup")
    public SingleResult<Long> signup(@ApiParam("회원가입 요청 DTO ") @RequestBody @Validated UserSignUpRequestDto userSignUpRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new UserException(UserErrorCode.NOT_VALID_REQUEST_PARAMETERS);
        }

        userSignUpRequestDto.setPassword(passwordEncoder.encode(userSignUpRequestDto.getPassword()));
        Long userId = userService.signup(userSignUpRequestDto);
        return responseService.getSingleResult(userId);
    }
}
