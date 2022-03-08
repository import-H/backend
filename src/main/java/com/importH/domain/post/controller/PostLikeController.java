package com.importH.domain.post.controller;


import com.importH.domain.post.service.PostLikeService;
import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.entity.User;
import com.importH.global.response.ResponseService;
import com.importH.global.response.CommonResult;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "6. PostLike")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts/{postId}/like")
public class PostLikeController {

    private final ResponseService responseService;
    private final PostLikeService postLikeService;


    @ApiOperation(value = "게시글 좋아요", notes = "postId 의 게시글 좋아요 요청을 보냅니다.")
    @PostMapping
    public CommonResult requestLike(@ApiIgnore @CurrentUser User user,
                                    @ApiParam(value = "게시글 ID" , example = "1") @PathVariable Long postId) {

        postLikeService.addLike(user,postId);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "게시글 좋아요 취소", notes = "postId 의 게시글 좋아요 취소 요청을 보냅니다.")
    @DeleteMapping
    public CommonResult requestUnLike(@ApiIgnore @CurrentUser User user,
                                    @ApiParam(value = "게시글 ID" , example = "1") @PathVariable Long postId) {

        postLikeService.cancelLike(user,postId);
        return responseService.getSuccessResult();
    }
}
