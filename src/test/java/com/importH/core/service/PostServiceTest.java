package com.importH.core.service;

import com.importH.core.AccountFactory;
import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.error.exception.PostException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

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
    Post post;

    @BeforeEach
    void before() {
        account = accountFactory.createNewAccount("test");
        post = postService.registerPost(account, 1, getRequest("테스트", "테스트 게시글 입니다.", "자바"));
    }

    @AfterEach
    void after() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("[성공] 게시글 등록 정상적인 요청")
    void registerPost_success() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트", "테스트 게시글 입니다.", "자바");

        // when
        Post post = postService.registerPost(account, 1, request);


        //then
        assertThat(post).hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("content", request.getContent());
        assertThat(post.getTags()).contains(tagService.findByTitle("자바"));
    }

    private PostDto.Request getRequest(String title, String content, String tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .content(content)
                .tags(List.of(TagDto.builder()
                        .name(tagName)
                        .build()))
                .build();
    }


    @Test
    @DisplayName("[성공] 게시글 조회 정상적인 요청")
    void getPost_success() throws Exception {
        // given
        Post post =  postService.registerPost(account, 1, getRequest("테스트", "테스트 게시글 입니다.", "자바"));

        // when
        PostDto.Response response = postService.getPost(account, 1, post.getId());

        //then
        assertThat(response)

                .hasFieldOrPropertyWithValue("responseInfo.id", post.getId())
                .hasFieldOrPropertyWithValue("responseInfo.title", post.getTitle())
                .hasFieldOrPropertyWithValue("responseInfo.content", post.getContent())
                .hasFieldOrPropertyWithValue("responseInfo.user.nickname", post.getAccount().getNickname())
                .hasFieldOrPropertyWithValue("responseInfo.user.profileImage", post.getAccount().getProfileImage())
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

        PostException postException = assertThrows(PostException.class, () -> postService.getPost(account, boardId, postId));

        //then
        assertThat(postException).hasMessageContaining(notFoundPost.getDescription());

    }


    @Test
    @DisplayName("[성공] 게시글 수정")
    void updatePost_success() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트2", "테스트 게시글 입니다.3", "자바1");

        // when
        postService.updatePost(account, 1, post.getId(), request);

        //then
        assertThat(post)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("content", request.getContent());

        Tag tagBefore = tagService.findByTitle("자바");
        Tag tagAfter = tagService.findByTitle("자바1");
        assertThat(post.getTags()).contains(tagAfter);
        assertThat(post.getTags()).doesNotContain(tagBefore);
    }

    @Test
    @DisplayName("[실패] 게시글 수정 - 글 작성자 아닌경우")
    void updatePost_fail() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트2", "테스트 게시글 입니다.3", "자바1");
        Account test2 = Account.builder().nickname("test2").build();
        // when
        PostException postException = assertThrows(PostException.class, () -> postService.updatePost(test2, 1, post.getId(), request));

        //then
        assertThat(postException).hasMessageContaining(PostErrorCode.NOT_ACCORD_ACCOUNT.getDescription());
    }

    @Test
    @DisplayName("[성공] 게시글 삭제")
    void deletePost_success() throws Exception {
        // given
        // when
        postService.deletePost(account, 1, post.getId());

        //then
        assertThat(postRepository.existsById(post.getId())).isFalse();

    }

    @Test
    @DisplayName("[실패] 게시글 삭제 - 글 작성자가 아닌경우")
    void deletePost_fail() throws Exception {
        // given
        Account test2 = Account.builder().nickname("test2").build();
        // when
        PostException postException = assertThrows(PostException.class, () -> postService.deletePost(test2, 1, post.getId()));

        //then
        assertThat(postException).hasMessageContaining(PostErrorCode.NOT_ACCORD_ACCOUNT.getDescription());

    }

    @Test
    @DisplayName("[성공] 전체 게시글 조회")
    void findAll_success() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            postService.registerPost(account, 1, getRequest("테스트", "테스트 게시글 입니다.", "자바"));
        }
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(1);

        //then
        assertThat(allPost.size()).isEqualTo(11);
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.id")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.user")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }


    @Test
    @DisplayName("[성공] 전체 게시글 좋아요 내림차순 으로 조회")
    void findAllOrderByLike_success() throws Exception {

        // when
        List<PostDto.ResponseAll> allList = postService.findAllPostOrderByLike(PageRequest.of(0,10));

        int maxLike = allList.stream().mapToInt(value -> value.getResponseInfo().getLikeCount()).max().getAsInt();

        //then
        assertThat(allList.size()).isEqualTo(10);
        assertThat(allList.get(0).getResponseInfo().getLikeCount()).isEqualTo(maxLike);

    }
}