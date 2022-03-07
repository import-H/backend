package com.importH.domain.post.controller;

import com.importH.domain.post.dto.PostDto;
import com.importH.domain.post.service.PostService;
import com.importH.domain.post.entity.Post;
import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.entity.User;
import com.importH.global.response.ResponseService;
import com.importH.global.response.CommonResult;
import com.importH.global.response.ListResult;
import com.importH.global.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static com.importH.domain.ControllerCommon.validParameter;

@Api(tags = "3. Post")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {

    private final ResponseService responseService;
    private final PostService postService;


    @GetMapping("/boards/{boardId}")
    @ApiOperation(value = "전체 게시글 조회", notes = "boardId 게시판에 게시글을 모두 조회합니다.")
    public ListResult<PostDto.ResponseAll> findAllPosts(@ApiParam(value = "게시판 유형", defaultValue = "free") @PathVariable String boardId,
                                                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return responseService.getListResult(postService.findAllPost(boardId,pageable));
    }

    @ApiOperation(value = "게시글 조회", notes = "postId 게시글을 조회합니다.")
    @GetMapping("/posts/{postId}")
    public SingleResult<PostDto.Response> findPost(@ApiIgnore @CurrentUser User user,
                                                   @ApiParam(value = "게시글 ID", defaultValue = "1") @PathVariable Long postId) {

        return responseService.getSingleResult(postService.getPost(user, postId));
    }

    
    @ApiOperation(value = "게시글 등록", notes = "type 게시판에 게시글을 등록합니다.")
    @PostMapping("/posts")
    public SingleResult<Long> savePost(@ApiIgnore @CurrentUser User user,
                                 @ApiParam(value = "게시글 요청 DTO") @RequestBody @Validated PostDto.Request postRequestDto,
                                 BindingResult bindingResult) {

        validParameter(bindingResult);
        Post post = postService.registerPost(user, postRequestDto);
        return responseService.getSingleResult(post.getId());
    }

    
    @ApiOperation(value = "게시글 수정", notes = "postId 게시글을 수정합니다.")
    @PutMapping("/posts/{postId}")
    public SingleResult<Long> updatePost(@ApiIgnore @CurrentUser User user,
                                         @ApiParam(value = "게시글 ID", defaultValue = "1") @PathVariable Long postId,
                                         @ApiParam(value = "게시글 요청 DTO") @RequestBody @Validated PostDto.Request postRequestDto,
                                         BindingResult bindingResult
    ) {

        validParameter(bindingResult);

        return responseService.getSingleResult(postService.updatePost(user, postId, postRequestDto));

    }

    
    @ApiOperation(value = "게시글 삭제", notes = "postId 게시글을 삭제합니다.")
    @DeleteMapping("/posts/{postId}")
    public CommonResult deletePost(@ApiIgnore @CurrentUser User user,
                                   @ApiParam(value = "게시글 ID", defaultValue = "1") @PathVariable Long postId) {
        postService.deletePost(user, postId);
        return responseService.getSuccessResult();
    }


}
