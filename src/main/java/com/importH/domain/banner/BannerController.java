package com.importH.domain.banner;

import com.importH.domain.banner.BannerDto.Request;
import com.importH.domain.banner.BannerDto.Response;
import com.importH.global.response.CommonResult;
import com.importH.global.response.ListResult;
import com.importH.global.response.ResponseService;
import com.importH.global.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.importH.domain.ControllerCommon.validParameter;


@Api(tags = "7. Banner")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/banners")
public class BannerController  {

    private final ResponseService responseService;
    private final BannerService bannerService;


    
    @ApiOperation(value = "배너 등록", notes = "배너를 등록합니다.")
    @PostMapping
    public SingleResult<Response> registerBanner(@ApiParam("배너 요청 Dto") @RequestBody @Validated Request request,
                                                 BindingResult bindingResult) {

        validParameter(bindingResult);
        return responseService.getSingleResult(bannerService.registerBanner(request));
    }

    @GetMapping
    @ApiOperation(value = "배너 가져오기", notes = "등록된 배너 정보들을 가져옵니다.")
    public ListResult<Response> getBanners() {
        return responseService.getListResult(bannerService.getBanners());
    }


    @ApiOperation(value = "배너 삭제", notes = "bannerId 에 해당하는 배너를 삭제합니다.")
    @DeleteMapping("/{bannerId}")
    public CommonResult deleteBanner(@ApiParam(value = "bannerId", example = "1") @PathVariable Long  bannerId) {

        bannerService.deleteBanner(bannerId);
        return responseService.getSuccessResult();
    }

}
