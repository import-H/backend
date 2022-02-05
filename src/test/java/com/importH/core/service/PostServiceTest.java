package com.importH.core.service;

import com.importH.core.AccountFactory;
import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.error.exception.PostException;
import org.h2.util.ThreadDeadlockDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    AccountFactory accountFactory;


    @Autowired
    TagService tagService;

    Account account;

    @BeforeEach
    void before() {
        account = accountFactory.createNewAccount("test");
    }

    @AfterEach
    void after() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("[성공] 게시글 등록 정상적인 요청")
    void registerPost_success() throws Exception {
        // given
        PostDto.Request request = getRequest();

        // when
        Long postId = postService.registerPost(account, 1, request);

        Post post = postService.findByPostId(postId);

        //then
        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post).hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("content", request.getContent());
        assertThat(post.getTags()).contains(tagService.findByTitle("자바"));
    }

    private PostDto.Request getRequest() {
        return PostDto.Request.
                builder()
                .title("테스트")
                .content("테스트 게시글 입니다.")
                .tags(List.of(TagDto.builder()
                        .name("자바")
                        .build()))
                .build();
    }


    @Test
    @DisplayName("[성공] 게시글 조회 정상적인 요청")
    void getPost_success() throws Exception {
        // given
        Long postId = postService.registerPost(account, 1, getRequest());
        Post post = postService.findByPostId(postId);

        // when
        PostDto.Response response = postService.getPost(1, postId);

        //then
        assertThat(response)

                .hasFieldOrPropertyWithValue("responseInfo.id", postId)
                .hasFieldOrPropertyWithValue("responseInfo.title", post.getTitle())
                .hasFieldOrPropertyWithValue("responseInfo.content", post.getContent())
                .hasFieldOrPropertyWithValue("responseInfo.author", post.getAccount().getNickname())
                .hasFieldOrPropertyWithValue("responseInfo.likeCount", post.getLikeCount())
                .hasFieldOrPropertyWithValue("responseInfo.viewCount", post.getViewCount());

        assertThat(response.getResponseInfo().getTags()).hasSameElementsAs(postService.getTagDtos(post));
        assertThat(response.getComments()).hasSameElementsAs(postService.getCommentDtos(post));
    }

    @Test
    @DisplayName("[실패] 게시글 조회 - 옳바르지 않은 게시글 번호")
    void getPost_fail() throws Exception {
        // given
        Long postId = 2L;
        int boardId = 1;

        // when
        PostErrorCode notFoundPost = PostErrorCode.NOT_FOUND_POST;

        PostException postException = assertThrows(PostException.class, () -> postService.getPost(boardId, postId));

        //then
        assertThat(postException).hasMessageContaining(notFoundPost.getDescription());

    }
}