package com.importH.controller;

import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.dto.sign.LoginDto;
import com.importH.core.dto.sign.SignupDto;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.error.exception.UserException;
import com.importH.core.model.response.CommonResult;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.SignService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "1. SignUp / Login")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SignController {

    private final SignService signService;
    private final ResponseService responseService;

    @ApiOperation(value = "로그인", notes = "로그인을 합니다.")
    @PostMapping("/login")
    public SingleResult<TokenDto> login(
            @ApiParam(value = "로그인 요청 DTO", required = true) @RequestBody @Validated LoginDto request
            ,BindingResult bindingResult
            ) {

        validParameter(bindingResult);

        return responseService.getSingleResult(signService.login(request.getEmail(), request.getPassword()));
    }

    @ApiOperation(value = "회원가입 ", notes = "회원가입을 합니다.")
    @PostMapping("/signup")
    public SingleResult<Long> signup(@ApiParam(value = "회원가입 요청 DTO " ,required = true) @RequestBody @Validated SignupDto userSignupDto, BindingResult bindingResult) {

        validParameter(bindingResult);

        Long userId = signService.signup(userSignupDto);
        return responseService.getSingleResult(userId);
    }

    @ApiOperation(value = "엑세스 , 리프레시 토큰 재발급",
            notes = "액세스 토큰 만료시 회원 검증 후 리프레쉬 토큰을 검증해서 액세스 토큰과 리프레시 토큰을 재발급합니다.")
    @PostMapping("/reissue")
    public SingleResult<TokenDto> reissue(
            @ApiParam(value = "토큰 재발급 요청 DTO", required = true) @RequestBody TokenDto tokenDto
            ) {
        return responseService.getSingleResult(signService.reissue(tokenDto));
    }


    @ApiOperation(value = "이메일 인증", notes = "회원가입 후 이메일 인증을 진행합니다.")
    @GetMapping("/check-email-token")
    public CommonResult checkEmailToken(@ApiParam(value = "인증토큰",required = true) @RequestParam String token,
                                        @ApiParam(value = "인증토큰",required = true) @RequestParam String email ) {

        signService.completeSignup(token, email);

        return responseService.getSuccessResult();
    }

    private void validParameter(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserException(UserErrorCode.NOT_VALID_REQUEST_PARAMETERS,
                    getErrorMessage(bindingResult.getAllErrors()));
        }
    }

    private String getErrorMessage(List<ObjectError> errors) {
        return errors.stream()
                .map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.toList())
                .toString();
    }
}
