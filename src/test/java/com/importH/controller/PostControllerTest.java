package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.comment.CommentDto;
import com.importH.domain.comment.CommentService;
import com.importH.domain.post.Post;
import com.importH.domain.post.PostDto;
import com.importH.domain.post.PostRepository;
import com.importH.domain.post.PostService;
import com.importH.domain.tag.TagDto;
import com.importH.domain.tag.TagRepository;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.CommonErrorCode;
import com.importH.global.error.code.PostErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostControllerTest {

    public static final String V_1_POSTS = "/v1/posts";
    private static final String FREE = "free";
    public static final String QUESTIONS = "questions";
    @Autowired
    PostService postService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostFactory postFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentService commentService;

    Post post;
    User user;

    @BeforeEach
    void before() {
        user = userRepository.findByNickname("test1").get();
        post = createPost(user);
        commentService.registerComment(post.getId(), user, CommentDto.Request.builder().content("테스트 댓글").build());
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 등록")
    void savePost_success() throws Exception {
        // given

        PostDto.Request request = getRequest("테스트", "테스트 게시글", "free", "자바");

        // when
        mockMvc.perform(post(V_1_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        //then
        assertEquals(postRepository.existsByTitle("테스트"), true);
        assertNotNull(tagRepository.findByName("자바"));
    }

    @Test
    @DisplayName("[실패] 로그인 하지 않은 유저 접근")
    void savePost_access_fail() throws Exception {

        // given
        mockMvc.perform(post(V_1_POSTS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entryPoint"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 게시글 등록 - 유효하지 않은 파라미터")
    void savePost_fail_parameter() throws Exception {
        // given
        PostDto.Request request = getRequest("", "", "free", "자바");
        // when
        CommonErrorCode errorCode = CommonErrorCode.NOT_VALID_PARAM;

        mockMvc.perform(post(V_1_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

        //then
        assertThat(postRepository.existsByTitle(request.getTitle())).isFalse();
        assertThat(tagRepository.findByName("자바")).isEmpty();

    }

    @Test
    @DisplayName("[성공] 단일 게시글 조회")
    void findPost_success() throws Exception {

        // when
        ResultActions perform = mockMvc.perform(get(V_1_POSTS + "/" + post.getId()));

        //then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.responseInfo.boardId").value(post.getType()))
                .andExpect(jsonPath("$.data.responseInfo.postId").value(post.getId()))
                .andExpect(jsonPath("$.data.responseInfo.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.responseInfo.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.responseInfo.nickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.data.responseInfo.profileImage").value(post.getUser().getProfileImage()))
                .andExpect(jsonPath("$.data.responseInfo.likeCount").value(post.getLikeCount()))
                .andExpect(jsonPath("$.data.responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.data.responseInfo.viewCount").value(post.getViewCount()))
                .andExpect(jsonPath("$.data.like").value(false))
                .andExpect(jsonPath("$.data.comments[*].*", hasSize(5)));

    }

    @Test
    @DisplayName("[성공] 단일 게시글 조회 - 익명 조회시 조회수 증가")
    void findPost_success_2() throws Exception {

        // given
        int currentViewCount = post.getViewCount();
        // when
        ResultActions perform = mockMvc.perform(get(V_1_POSTS + "/" + post.getId()));

        //then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.responseInfo.boardId").value(post.getType()))
                .andExpect(jsonPath("$.data.responseInfo.postId").value(post.getId()))
                .andExpect(jsonPath("$.data.responseInfo.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.responseInfo.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.responseInfo.nickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.data.responseInfo.profileImage").value(post.getUser().getProfileImage()))
                .andExpect(jsonPath("$.data.responseInfo.likeCount").value(post.getLikeCount()))
                .andExpect(jsonPath("$.data.responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.data.responseInfo.viewCount").value(currentViewCount + 1))
                .andExpect(jsonPath("$.data.like").value(false))
                .andExpect(jsonPath("$.data.comments[*].*", hasSize(5)));

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 단일 게시글 조회 - 유저 조회시 조회수 증가")
    void findPost_success_3() throws Exception {

        // given
        int currentViewCount = post.getViewCount();
        // when
        ResultActions perform = mockMvc.perform(get(V_1_POSTS + "/" + post.getId()));

        //then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.responseInfo.boardId").value(post.getType()))
                .andExpect(jsonPath("$.data.responseInfo.postId").value(post.getId()))
                .andExpect(jsonPath("$.data.responseInfo.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.responseInfo.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.responseInfo.nickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.data.responseInfo.profileImage").value(post.getUser().getProfileImage()))
                .andExpect(jsonPath("$.data.responseInfo.likeCount").value(post.getLikeCount()))
                .andExpect(jsonPath("$.data.responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.data.responseInfo.viewCount").value(currentViewCount + 1))
                .andExpect(jsonPath("$.data.like").value(false))
                .andExpect(jsonPath("$.data.comments[*].*", hasSize(5)));

    }

    @Test
    @WithAccount("test1")
    @DisplayName("[성공] 단일 게시글 조회 - 같은 유저시 조회수 증가 X")
    void findPost_success_4() throws Exception {

        // given
        int currentViewCount = post.getViewCount();
        // when
        ResultActions perform = mockMvc.perform(get(V_1_POSTS + "/" + post.getId()));

        //then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.responseInfo.boardId").value(post.getType()))
                .andExpect(jsonPath("$.data.responseInfo.postId").value(post.getId()))
                .andExpect(jsonPath("$.data.responseInfo.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.responseInfo.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.responseInfo.nickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.data.responseInfo.profileImage").value(post.getUser().getProfileImage()))
                .andExpect(jsonPath("$.data.responseInfo.likeCount").value(post.getLikeCount()))
                .andExpect(jsonPath("$.data.responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.data.responseInfo.viewCount").value(currentViewCount))
                .andExpect(jsonPath("$.data.like").value(false))
                .andExpect(jsonPath("$.data.comments[*].*", hasSize(5)));

    }



    @Test
    @DisplayName("[실패] 단일 게시글 조회 - 옳바르지 않은 게시글 ID")
    void findPost_fail() throws Exception {
        //given
        PostErrorCode notFoundPost = PostErrorCode.NOT_FOUND_POST;

        // when
        ResultActions perform = mockMvc.perform(get(V_1_POSTS + "/999999"));


        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(notFoundPost.getDescription()));

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 수정")
    void updatePost_success() throws Exception {
        // given
        post = createPost(userRepository.findByNickname("테스트").get());
        PostDto.Request request = getRequest("테스트2", "테스트2", "free", "스터디1","자바1");

        // when
        ResultActions perform = mockMvc.perform(put(V_1_POSTS + "/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 게시글 수정 - 작성자 아닌경우")
    void updatePost_fail() throws Exception {
        // given
        PostDto.Request request = getRequest("테스트2", "테스트2", "free", "스터디1","자바1");
        PostErrorCode notAccordAccount = PostErrorCode.NOT_ACCORD_ACCOUNT;

        // when
        ResultActions perform = mockMvc.perform(put(V_1_POSTS + "/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(notAccordAccount.getDescription()));

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 삭제")
    void deletePost_success() throws Exception {
        // given
        post = createPost(userRepository.findByNickname("테스트").get());

        // when

        mockMvc.perform(delete(V_1_POSTS + "/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        //then
        assertThat(postRepository.existsById(post.getId())).isFalse();

    }

    private Post createPost(User user) {
        return postFactory.createPost(user, getRequest("test", "test게시글", "free", "스터디", "자바2"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 게시글 삭제 - 게시글 작성자가 아닌경우")
    void deletePost_fail() throws Exception {
        // given
        PostErrorCode notAccordAccount = PostErrorCode.NOT_ACCORD_ACCOUNT;

        // when

        mockMvc.perform(delete(V_1_POSTS + "/" + post.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(notAccordAccount.getDescription()));

        //then
        assertThat(postRepository.existsById(post.getId())).isTrue();

    }


    @Test
    @DisplayName("[성공] 전체 게시글 조회 - 자유 게시판 / 전체 게시글이 페이징 갯수보다 적을때")
    void findAllPost_Success_01() throws Exception {
        // given
        for (int i = 0; i < 8; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.",FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", QUESTIONS));
        }
        // when
        ResultActions perform = mockMvc.perform(get("/v1/boards/"+FREE+"?size=10"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.boardId").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.postId").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.title").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.content").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.nickname").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.profileImage").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.likeCount").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.viewCount").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(postRepository.countByType(FREE))));
    }

    @Test
    @DisplayName("[성공] 전체 게시글 조회 - 자유 게시판 / 전체 게시글이 페이징 갯수보다 많을때")
    void findAllPost_Success_02() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.",FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("테스트", "테스트 게시글 입니다.", QUESTIONS));
        }
        String limit = "5";

        // when
        ResultActions perform = mockMvc.perform(get("/v1/boards/"+FREE+ "?size=" + limit));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.boardId").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.postId").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.title").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.content").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.nickname").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.profileImage").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.likeCount").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.viewCount").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(Integer.valueOf(limit))));
    }



    private PostDto.Request getRequest(String title, String content, String type, String... tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .type(type)
                .content(content)
                .tags(Arrays.stream(tagName).map(name ->TagDto.builder().name(name).build()).collect(Collectors.toList()))
                .build();
    }
}