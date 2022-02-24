package com.importH.core.service;

import com.importH.core.UserFactory;
import com.importH.domain.post.Post;
import com.importH.domain.post.PostDto;
import com.importH.domain.post.PostDto.Response;
import com.importH.domain.post.PostRepository;
import com.importH.domain.post.PostService;
import com.importH.domain.tag.Tag;
import com.importH.domain.tag.TagDto;
import com.importH.domain.tag.TagService;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.exception.PostException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostServiceTest {

    public static final String FREE = "free";
    public static final String QUESTIONS = "questions";
    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    UserFactory userFactory;

    @Autowired
    TagService tagService;

    User user;
    Post post;

    @BeforeEach
    void before() {
        user = userFactory.createNewAccount("test", "test" + "@email.com", "pathId", true);
        post = postService.registerPost(user , getRequest("테스트", "테스트 게시글 입니다.", "자바", FREE));
    }

    @AfterEach
    void after() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("[성공] 게시글 등록 정상적인 요청")
    void registerPost_success() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트", "테스트 게시글 입니다.", "자바", FREE);

        // when
        Post post = postService.registerPost(user, request);


        //then
        assertThat(post).hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("content", request.getContent());
        assertThat(post.getTags()).contains(tagService.findByTitle("자바"));
    }

    @Test
    @DisplayName("[실패] 게시글 등록 - 존재하지 않는 게시판")
    void registerPost_fail() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트", "테스트 게시글 입니다.", "자바", "FREES");

        // when
        PostException exception = assertThrows(PostException.class, () -> postService.registerPost(user, request));


        //then
        assertThat(exception).hasMessageContaining(exception.getErrorMessage());
    }

    private PostDto.Request getRequest(String title, String content, String tagName, String type) {
        return PostDto.Request.
                builder()
                .title(title)
                .type(type)
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
        Post post =  postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", "자바", FREE));

        // when
        Response response = postService.getPost(user, post.getId());

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("responseInfo.boardId", post.getType())
                .hasFieldOrPropertyWithValue("responseInfo.postId", post.getId())
                .hasFieldOrPropertyWithValue("responseInfo.title", post.getTitle())
                .hasFieldOrPropertyWithValue("responseInfo.content", post.getContent())
                .hasFieldOrPropertyWithValue("responseInfo.nickname", post.getUser().getNickname())
                .hasFieldOrPropertyWithValue("responseInfo.profileImage", post.getUser().getProfileImage())
                .hasFieldOrPropertyWithValue("responseInfo.likeCount", post.getLikeCount())
                .hasFieldOrPropertyWithValue("responseInfo.viewCount", post.getViewCount());

        assertThat(response.getResponseInfo().getTags()).hasSameElementsAs(tagService.getTagDtos(post.getTags()));
        assertThat(response.getComments()).hasSameElementsAs(postService.getCommentDtos(post));
    }

    @Test
    @DisplayName("[실패] 게시글 조회 - 옳바르지 않은 게시글 번호")
    void getPost_fail() throws Exception {
        // given
        Long postId = 99999999L;

        // when
        PostErrorCode notFoundPost = PostErrorCode.NOT_FOUND_POST;

        PostException postException = assertThrows(PostException.class, () -> postService.getPost(user, postId));

        //then
        assertThat(postException).hasMessageContaining(notFoundPost.getDescription());

    }


    @Test
    @DisplayName("[성공] 게시글 수정")
    void updatePost_success() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트2", "테스트 게시글 입니다.3", "자바1", FREE);

        // when
        postService.updatePost(user, post.getId(), request);

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
        PostDto.Request request = getRequest("테스트2", "테스트 게시글 입니다.3", "자바1", FREE);
        User test2 = User.builder().nickname("test2").build();
        // when
        PostException postException = assertThrows(PostException.class, () -> postService.updatePost(test2, post.getId(), request));

        //then
        assertThat(postException).hasMessageContaining(PostErrorCode.NOT_ACCORD_ACCOUNT.getDescription());
    }

    @Test
    @DisplayName("[성공] 게시글 삭제")
    void deletePost_success() throws Exception {
        // given

        // when
        postService.deletePost(user, post.getId());

        //then
        assertThat(postRepository.existsById(post.getId())).isFalse();

    }

    @Test
    @DisplayName("[실패] 게시글 삭제 - 글 작성자가 아닌경우")
    void deletePost_fail() throws Exception {
        // given
        User test2 = User.builder().nickname("test2").build();
        // when
        PostException postException = assertThrows(PostException.class, () -> postService.deletePost(test2, post.getId()));

        //then
        assertThat(postException).hasMessageContaining(PostErrorCode.NOT_ACCORD_ACCOUNT.getDescription());

    }

    @Test
    @DisplayName("[성공] 전체 게시글 조회 - 자유 게시판 게시글 작성 날짜 최신순으로 조회 ")
    void findAll_success_01() throws Exception {
        // given
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", "자바", FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", "자바", QUESTIONS));
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(FREE,pageRequest);

        //then
        assertThat(allPost.size()).isEqualTo(postRepository.countByType(FREE));
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }

    @Test
    @DisplayName("[성공] 전체 게시글 조회 - 모든 게시판글 작성 날짜 최신순으로 조회 ")
    void findAll_success_02() throws Exception {
        // given
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", "자바", FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", "자바", QUESTIONS));
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(null,pageRequest);

        //then
        assertThat(allPost.size()).isEqualTo(10);
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }


    @Test
    @DisplayName("[성공] 전체 게시글 조회 - 좋아요 내림차순 으로 조회")
    void findAllOrderByLike_success() throws Exception {

        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").descending());

        // when
        List<PostDto.ResponseAll> allList = postService.findAllPost(null, pageRequest);

        int maxLike = allList.stream().mapToInt(value -> value.getResponseInfo().getLikeCount()).max().getAsInt();

        //then
        assertThat(allList.size()).isEqualTo(10);
        assertThat(allList.get(0).getResponseInfo().getLikeCount()).isEqualTo(maxLike);
        allList.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));

    }

    @Test
    @DisplayName("유저 탈퇴 후 게시글 조회")
    void getPostByDeletedUser() throws Exception {
        // given
        user.delete();

        // when
        Response post = postService.getPost(user, this.post.getId());

        //then
        assertThat(post.getResponseInfo())
                .hasFieldOrPropertyWithValue("nickname","삭제된 계정")
                .hasFieldOrPropertyWithValue("profileImage","N");
    }



}