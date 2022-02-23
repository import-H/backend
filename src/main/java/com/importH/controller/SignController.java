package com.importH.controller;

import com.importH.core.domain.user.CurrentUser;
import com.importH.core.domain.user.User;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.dto.sign.LoginDto;
import com.importH.core.dto.sign.SignupDto;
import com.importH.core.model.response.CommonResult;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.OauthService;
import com.importH.core.service.SignService;
import com.importH.core.service.response.ResponseService;
import com.importH.error.code.UserErrorCode;
import com.importH.error.exception.UserException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.importH.controller.common.ControllerCommon.validParameter;

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
