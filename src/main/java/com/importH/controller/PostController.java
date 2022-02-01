package com.importH.controller;

import com.importH.config.security.CurrentAccount;
import com.importH.core.domain.account.Account;
import com.importH.core.dto.post.PostRequestDto;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.error.exception.PostException;
import com.importH.core.model.response.SingleResult;
import com.importH.core.service.PostService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "3. Post")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
public class PostController {

    private final ResponseService responseService;
    private final PostService postService;
//    @GetMapping("/{boardId}/posts")
//    public ListResult<PostResponseDto> findAllPosts(@PathVariable String boardId) {
//
//        return responseService.getListResult(postService.findAllPost(boardId));
//    }

    @PostMapping("/{boardId}/posts")
    public SingleResult<Long> savePost(@CurrentAccount Account account, @PathVariable int boardId, @RequestBody @Validated PostRequestDto postRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new PostException(PostErrorCode.POST_NOT_VALIDATE);
        }

        return responseService.getSingleResult(postService.registerPost(account,boardId,postRequestDto));
    }


}
