package com.importH.controller;

import com.importH.core.domain.user.CurrentUser;
import com.importH.core.domain.user.User;
import com.importH.core.dto.user.PasswordDto;
import com.importH.core.dto.user.UserDto.Request;
import com.importH.core.dto.user.UserDto.Response;
import com.importH.core.model.response.CommonResult;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.UserService;
import com.importH.core.service.response.ResponseService;
import com.importH.error.code.UserErrorCode;
import com.importH.error.exception.UserException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"2. User"})
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ResponseService responseService;
    private final UserService userService;

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 단건 검색", notes = "userId로 회원을 조회합니다.")
    @GetMapping("/{userId}")
    public SingleResult<Response> findUserById
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user) {
        return responseService.getSingleResult(userService.findUserById(userId, user));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 정보 수정", notes = "userId 회원 정보를 수정합니다.")
    @PutMapping("/{userId}")
    public SingleResult<Response> updateUser
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user,
             @ApiParam(value = "유저 요청 DTO") @RequestBody @Validated Request request,
             BindingResult bindingResult) {

        validParameter(bindingResult);
        return responseService.getSingleResult(userService.updateUser(userId, user, request));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 탈퇴", notes = "userId 회원이 탈퇴 합니다.")
    @DeleteMapping("/{userId}")
    public CommonResult deleteUser
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user) {

        userService.deleteUser(userId, user);

        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "비밀번호 변경", notes = "userId 회원의 비밀번호를 변경합니다.")
    @PutMapping("/{userId}/updatePassword")
    public CommonResult updatePassword
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user,
             @ApiParam(value = "비밀번호 요청 DTO") @RequestBody @Validated PasswordDto.Request request,
             BindingResult bindingResult)  {

        validParameter(bindingResult);

        userService.updatePassword(userId, user,request);

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

