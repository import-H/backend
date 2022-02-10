package com.importH.controller;

import com.importH.config.security.CurrentUser;
import com.importH.core.domain.user.User;
import com.importH.core.dto.post.CommentDto;
import com.importH.core.model.response.CommonResult;
import com.importH.core.service.CommentService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "5. Comments")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts/{postsId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 등록", notes = "postsId 게시글에 댓글을 등록합니다.")
    @PostMapping
    public CommonResult saveComment(@ApiParam(value = "게시글 ID", example = "1") @PathVariable Long  postsId,
                                    @ApiIgnore @CurrentUser User user,
                                    @ApiParam("댓글 요청 Dto") @RequestBody CommentDto.Request commentDto) {
        commentService.registerComment(postsId, user, commentDto);
        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 수장", notes = "postsId 게시글에 댓글을 수정합니다.")
    @PutMapping("/{commentId}")
    public CommonResult updateComment(@ApiParam(value = "게시글 ID", example = "1") @PathVariable Long postsId,
                                      @ApiParam(value = "댓글 ID", example = "1") @PathVariable Long commentId,
                                      @ApiIgnore @CurrentUser User user,
                                      @ApiParam("댓글 요청 Dto") @RequestBody CommentDto.Request commentDto) {
        commentService.updateComment(postsId,commentId, user, commentDto);
        return responseService.getSuccessResult();
    }


    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Authorization",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 삭제", notes = "postsId 게시글에 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public CommonResult deleteComment(@ApiParam(value = "게시글 ID", example = "1") @PathVariable Long postsId,
                                      @ApiParam(value = "댓글 ID", example = "1") @PathVariable Long commentId,
                                      @ApiIgnore @CurrentUser User user) {
        commentService.deleteComment(postsId, commentId, user);
        return responseService.getSuccessResult();

    }

}
