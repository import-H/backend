package com.importH.controller;

import com.importH.core.dto.post.PostDto;
import com.importH.core.model.response.CommonResult;
import com.importH.core.model.response.ListResult;
import com.importH.core.service.PostService;
import com.importH.core.service.response.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "5. Home")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/main")
public class HomeController {

    private final ResponseService responseService;
    private final PostService postService;

    @ApiOperation(value = "메인 화면 게시글 좋아요순으로 조회", notes = "전체 게시글을 좋아요 순으로 조회 합니다.")
    @GetMapping
    public ListResult<PostDto.ResponseAll> mainPosts(Pageable pageable) {

        return responseService.getListResult(postService.findAllPostOrderByLike(pageable));
    }


}
