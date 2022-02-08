package com.importH.controller;


import com.importH.config.security.CurrentAccount;
import com.importH.core.domain.account.Account;
import com.importH.core.model.response.CommonResult;
import com.importH.core.service.PostLikeService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "6. PostLike")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts/{postId}/like")
public class PostLikeController {

    private final ResponseService responseService;
    private final PostLikeService postLikeService;


    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "게시글 좋아요 요청", notes = "postId 의 게시글 좋아요 요청을 보냅니다.")
    @PostMapping
    public CommonResult requestLike(@ApiIgnore @CurrentAccount Account account,
                                    @ApiParam(value = "게시글 ID" , example = "1") @PathVariable Long postId) {

        postLikeService.changeLike(account,postId);
        return responseService.getSuccessResult();
    }
}
