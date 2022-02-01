package com.importH.controller;

import com.importH.config.security.CurrentAccount;
import com.importH.core.domain.account.Account;
import com.importH.core.dto.sign.UserResponseDto;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.response.ResponseService;
import com.importH.core.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"2. User"})
@RestController
@RequestMapping("/v1")
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
    @GetMapping("/user/id/{userId}")
    public SingleResult<UserResponseDto> findUserById
            (@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId,
             @CurrentAccount Account account) {
        return responseService.getSingleResult(userService.findUserById(userId));
    }

}

