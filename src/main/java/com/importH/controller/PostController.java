package com.importH.controller;

import com.importH.core.domain.user.CurrentUser;
import com.importH.core.domain.user.User;
import com.importH.core.dto.post.PostDto;
import com.importH.core.model.response.CommonResult;
import com.importH.core.model.response.ListResult;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.PostService;
import com.importH.core.service.response.ResponseService;
import com.importH.error.code.PostErrorCode;
import com.importH.error.exception.PostException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "3. Post")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards/{boardId}/posts")
public class PostController {

    private final ResponseService responseService;
    private final PostService postService;


    @GetMapping
    @ApiOperation(value = "전체 게시글 조회", notes = "boardId 게시판에 게시글을 모두 조회합니다.")
    public ListResult<PostDto.ResponseAll> findAllPosts(@ApiParam(value = "게시판 유형", defaultValue = "free") @PathVariable String boardId) {
        //TODO 페이징

        return responseService.getListResult(postService.findAllPost(boardId));
    }

    @ApiOperation(value = "게시글 조회", notes = "boardId 게시판에 postId 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public SingleResult<PostDto.Response> findPost(@ApiIgnore @CurrentUser User user,
                                                   @ApiParam(value = "게시판 유형", defaultValue = "free") @PathVariable String boardId,
                                                   @ApiParam(value = "게시글 ID", defaultValue = "1") @PathVariable Long postId) {

        return responseService.getSingleResult(postService.getPost(user,boardId, postId));
    }

    
    @ApiOperation(value = "게시글 등록", notes = "boardId 게시판에 게시글을 등록합니다.")
    @PostMapping
    public CommonResult savePost(@ApiIgnore @CurrentUser User user,
                                 @ApiParam(value = "게시판 유형", defaultValue = "free") @PathVariable String boardId,
                                 @ApiParam(value = "게시글 요청 DTO") @RequestBody @Validated PostDto.Request postRequestDto,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new PostException(PostErrorCode.NOT_VALIDATE_PARAM);
        }
        postService.registerPost(user, boardId, postRequestDto);
        return responseService.getSuccessResult();
    }

    
    @ApiOperation(value = "게시글 수정", notes = "boardId 게시판에 postId 게시글을 수정합니다.")
    @PutMapping("/{postId}")
    public SingleResult<Long> updatePost(@ApiIgnore @CurrentUser User user,
                                         @ApiParam(value = "게시판 유형",defaultValue = "free") @PathVariable String boardId,
                                         @ApiParam(value = "게시글 ID", defaultValue = "1") @PathVariable Long postId,
                                         @ApiParam(value = "게시글 요청 DTO") @RequestBody @Validated PostDto.Request postRequestDto,
                                         BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            throw new PostException(PostErrorCode.NOT_VALIDATE_PARAM);
        }

        return responseService.getSingleResult(postService.updatePost(user, boardId, postId, postRequestDto));

    }

    
    @ApiOperation(value = "게시글 삭제", notes = "boardId 게시판에 postId 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public CommonResult deletePost(@ApiIgnore @CurrentUser User user,
                                   @ApiParam(value = "게시판 유형", defaultValue = "free") @PathVariable String boardId,
                                   @ApiParam(value = "게시글 ID", defaultValue = "1") @PathVariable Long postId) {
        postService.deletePost(user, boardId, postId);
        return responseService.getSuccessResult();
    }


}
