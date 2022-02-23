package com.importH.domain.user.controller;

import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.dto.PasswordDto;
import com.importH.domain.user.dto.UserDto;
import com.importH.domain.user.dto.UserDto.Request;
import com.importH.domain.user.dto.UserDto.Response;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.service.UserService;
import com.importH.global.response.ResponseService;
import com.importH.global.response.CommonResult;
import com.importH.global.response.ListResult;
import com.importH.global.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static com.importH.domain.ControllerCommon.validParameter;

@Api(tags = {"2. User"})
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ResponseService responseService;
    private final UserService userService;


    @ApiOperation(value = "회원 단건 검색", notes = "userId로 회원을 조회합니다.")
    @GetMapping("/{userId}")
    public SingleResult<Response> findUserById
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user) {
        return responseService.getSingleResult(userService.findUserById(userId, user));
    }


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


    @ApiOperation(value = "회원 탈퇴", notes = "userId 회원이 탈퇴 합니다.")
    @DeleteMapping("/{userId}")
    public CommonResult deleteUser
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user) {

        userService.deleteUser(userId, user);

        return responseService.getSuccessResult();
    }


    @ApiOperation(value = "비밀번호 변경", notes = "userId 회원의 비밀번호를 변경합니다.")
    @PutMapping("/{userId}/updatePassword")
    public CommonResult updatePassword
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @ApiIgnore @CurrentUser User user,
             @ApiParam(value = "비밀번호 요청 DTO") @RequestBody @Validated PasswordDto.Request request,
             BindingResult bindingResult) {

        validParameter(bindingResult);

        userService.updatePassword(userId, user, request);

        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "이메일 인증된 유저들 정보 불러오기", notes = "모든 유저 정보를 조회 합니다.")
    @GetMapping
    public ListResult<UserDto.Response_findAllUsers> findAllUsers(Pageable pageable) {
        return responseService.getListResult(userService.findAllUsers(pageable));
    }



}

