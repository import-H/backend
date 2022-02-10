package com.importH.controller;

import com.importH.config.security.CurrentAccount;
import com.importH.core.domain.user.User;
import com.importH.core.dto.user.UserDto.Request;
import com.importH.core.dto.user.UserDto.Response;
import com.importH.core.error.code.UserErrorCode;
import com.importH.core.error.exception.UserException;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.UserService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
             @ApiIgnore @CurrentAccount User user) {
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
             @ApiIgnore @CurrentAccount User user,
             @ApiParam(value = "유저 요청 DTO") @RequestBody @Validated Request request,
             BindingResult bindingResult) {

        validParameter(bindingResult);
        return responseService.getSingleResult(userService.updateUser(userId, user, request));
    }

    private void validParameter(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserException(UserErrorCode.NOT_VALID_REQUEST_PARAMETERS,
                    bindingResult.getAllErrors().stream()
                            .map(objectError -> objectError.getDefaultMessage())
                            .collect(Collectors.toList())
                            .toString());
        }
    }

}

