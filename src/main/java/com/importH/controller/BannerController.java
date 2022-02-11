package com.importH.controller;

import com.importH.config.security.CurrentUser;
import com.importH.core.domain.user.User;
import com.importH.core.dto.banner.BannerDto.Request;
import com.importH.core.dto.banner.BannerDto.Response;
import com.importH.core.error.exception.BannerException;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.BannerService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.stream.Collectors;

import static com.importH.core.error.code.BannerErrorCode.NOT_AUTHORITY_REGISTER;

@Api(tags = "7. Banner")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/banners")
public class BannerController  {

    private final ResponseService responseService;
    private final BannerService bannerService;


    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "배너 등록", notes = "배너를 등록합니다.")
    @PostMapping
    public SingleResult<Response> registerBanner(@ApiIgnore @CurrentUser User user,
                                                 @ApiParam("배너 요청 Dto") @RequestBody @Validated Request request,
                                                 BindingResult bindingResult) {

        validParameter(bindingResult);
        return responseService.getSingleResult(bannerService.registerBanner(request, user.getRole()));
    }

    private void validParameter(BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                throw new BannerException(NOT_AUTHORITY_REGISTER, getErrorMessage(bindingResult.getAllErrors()));
            }
    }

    private String getErrorMessage(List<ObjectError> errors) {
        return errors.stream()
                .map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.toList())
                .toString();
    }
}
