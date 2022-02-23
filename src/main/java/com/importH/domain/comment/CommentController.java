package com.importH.domain.comment;

import com.importH.domain.comment.CommentDto.Request;
import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.entity.User;
import com.importH.global.response.ResponseService;
import com.importH.global.response.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    
    @ApiOperation(value = "댓글 등록", notes = "postsId 게시글에 댓글을 등록합니다.")
    @PostMapping
    public CommonResult saveComment(@ApiParam(value = "게시글 ID", example = "1") @PathVariable Long  postsId,
                                    @ApiIgnore @CurrentUser User user,
                                    @ApiParam("댓글 요청 Dto") @RequestBody Request commentDto) {
        commentService.registerComment(postsId, user, commentDto);
        return responseService.getSuccessResult();
    }

    
    @ApiOperation(value = "댓글 수장", notes = "postsId 게시글에 댓글을 수정합니다.")
    @PutMapping("/{commentId}")
    public CommonResult updateComment(@ApiParam(value = "게시글 ID", example = "1") @PathVariable Long postsId,
                                      @ApiParam(value = "댓글 ID", example = "1") @PathVariable Long commentId,
                                      @ApiIgnore @CurrentUser User user,
                                      @ApiParam("댓글 요청 Dto") @RequestBody Request commentDto) {
        commentService.updateComment(postsId,commentId, user, commentDto);
        return responseService.getSuccessResult();
    }


    
    @ApiOperation(value = "댓글 삭제", notes = "postsId 게시글에 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public CommonResult deleteComment(@ApiParam(value = "게시글 ID", example = "1") @PathVariable Long postsId,
                                      @ApiParam(value = "댓글 ID", example = "1") @PathVariable Long commentId,
                                      @ApiIgnore @CurrentUser User user) {
        commentService.deleteComment(postsId, commentId, user);
        return responseService.getSuccessResult();

    }

}
