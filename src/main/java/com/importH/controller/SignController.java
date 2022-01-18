package com.importH.controller;

import com.importH.config.security.JwtProvider;
import com.importH.dto.jwt.TokenDto;
import com.importH.dto.jwt.TokenRequestDto;
import com.importH.dto.sign.UserLoginRequestDto;
import com.importH.dto.user.UserLoginResponseDto;
import com.importH.dto.sign.UserSignUpRequestDto;
import com.importH.error.code.UserErrorCode;
import com.importH.error.exception.UserException;
import com.importH.model.response.SingleResult;
import com.importH.service.user.UserService;
import com.importH.service.response.ResponseService;
import com.importH.service.sign.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "1. SignUp / Login")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SignController {

    private final UserService userService;
    private final SignService signService;
    private final JwtProvider jwtProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation(value = "로그인", notes = "로그인을 합니다.")
    @PostMapping("/login")
    public SingleResult<TokenDto> login(
            @ApiParam(value = "로그인 요청 DTO", required = true) @RequestBody UserLoginRequestDto userLoginRequestDto
            ) {
        TokenDto tokenDto = signService.login(userLoginRequestDto.getEmail(), userLoginRequestDto.getPassword());
        return responseService.getSingleResult(tokenDto);
    }

    @ApiOperation(value = "회원가입 ", notes = "회원가입을 합니다.")
    @PostMapping("/signup")
    public SingleResult<Long> signup(@ApiParam(value = "회원가입 요청 DTO " ,required = true) @RequestBody @Validated UserSignUpRequestDto userSignUpRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new UserException(UserErrorCode.NOT_VALID_REQUEST_PARAMETERS);
        }

        userSignUpRequestDto.setPassword(passwordEncoder.encode(userSignUpRequestDto.getPassword()));
        Long userId = signService.signup(userSignUpRequestDto);
        return responseService.getSingleResult(userId);
    }

    @ApiOperation(value = "엑세스 , 리프레시 토큰 재발급",
            notes = "액세스 토큰 만료시 회원 검증 후 리프레쉬 토큰을 검증해서 액세스 토큰과 리프레시 토큰을 재발급합니다.")
    @PostMapping("/reissue")
    public SingleResult<TokenDto> reissue(
            @ApiParam(value = "토큰 재발급 요청 DTO", required = true) @RequestBody TokenRequestDto tokenRequestDto
            ) {
        return responseService.getSingleResult(signService.reissue(tokenRequestDto));
    }
}
