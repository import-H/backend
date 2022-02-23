package com.importH.domain.user.controller;

import com.importH.domain.user.token.TokenDto;
import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.dto.LoginDto;
import com.importH.domain.user.dto.SignupDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.service.OauthService;
import com.importH.domain.user.service.SignService;
import com.importH.global.response.ResponseService;
import com.importH.global.response.CommonResult;
import com.importH.global.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static com.importH.domain.ControllerCommon.validParameter;

@Api(tags = "1. SignUp / Login")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SignController {

    private final SignService signService;
    private final ResponseService responseService;

    private final OauthService oauthService;

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

    @GetMapping("/oauth2/code/{provider}")
    public ResponseEntity<TokenDto> login(@PathVariable String provider, @RequestParam String code) {
        TokenDto loginResponse = oauthService.socialLogin(provider, code);
        return ResponseEntity.ok().body(loginResponse);
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
    @GetMapping("/email-token")
    public CommonResult checkEmailToken(@ApiParam(value = "이메일 인증 토큰") @RequestParam String token,
                                        @ApiParam(value = "이메일") @RequestParam String email ) {

        signService.completeSignup(token, email);

        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "이메일 인증 재전송", notes = "인증 이메일을 재전송 합니다.")
    @PostMapping("/email-token")
    public CommonResult resendEmailToken(@ApiIgnore @CurrentUser User user) {

        if (user == null) {
            //TODO null 에 대한 처리
        }
        signService.resendConfirmEmail(user.getEmail());

        return responseService.getSuccessResult();
    }

}
