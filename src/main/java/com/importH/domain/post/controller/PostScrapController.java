package com.importH.domain.post.controller;

import com.importH.domain.post.service.PostScrapService;
import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.entity.User;
import com.importH.global.response.CommonResult;
import com.importH.global.response.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "9. PostScrap")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts/{postId}/scrap")
public class PostScrapController {

    private final PostScrapService postScrapService;
    private final ResponseService responseService;

    @ApiOperation(value = "게시글 스크랩 요청", notes = "postId 의 게시글 스크랩 요청을 보냅니다.")
    @PostMapping
    public CommonResult requestScrap(@ApiIgnore @CurrentUser User user,
                                    @ApiParam(value = "게시글 ID" , example = "1") @PathVariable Long postId) {


        postScrapService.scrap(postId, user);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "게시글 스크랩 취소", notes = "postId 의 게시글 스크랩 취소 요청을 보냅니다.")
    @DeleteMapping
    public CommonResult requestScrapCancel(@ApiIgnore @CurrentUser User user,
                                     @ApiParam(value = "게시글 ID" , example = "1") @PathVariable Long postId) {


        postScrapService.cancelScrap(postId, user);
        return responseService.getSuccessResult();
    }

}
