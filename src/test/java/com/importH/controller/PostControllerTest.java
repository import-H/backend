package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.comment.CommentDto;
import com.importH.domain.comment.CommentService;
import com.importH.domain.post.dto.PostDto;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostType;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.post.service.PostService;
import com.importH.domain.tag.TagDto;
import com.importH.domain.tag.TagRepository;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.CommonErrorCode;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
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
import static org.hamcrest.Matchers.*;
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
        commentService.registerComment(post.getId(), user, CommentDto.Request.builder().content("????????? ??????").build());
    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ?????? ?????????")
    void savePost_success() throws Exception {
        // given

        PostDto.Request request = getRequest("?????????", "????????? ?????????", "free", "??????");

        // when
        mockMvc.perform(post(V_1_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        //then
        assertEquals(postRepository.existsByTitle("?????????"), true);
        assertNotNull(tagRepository.findByName("??????"));
    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ?????? ?????????")
    void savePost_success_pathId() throws Exception {

        // given
        user = userRepository.findByNickname("?????????").get();
        PostDto.Request request = getRequest("?????????", "????????? ?????????", user.getPathId(), "??????");

        // when
        mockMvc.perform(post(V_1_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        //then
        assertEquals(postRepository.existsByTitle("?????????"), true);
        assertNotNull(tagRepository.findByName("??????"));
    }

    @Test
    @DisplayName("[??????] ????????? ?????? ?????? ?????? ??????")
    void savePost_access_fail() throws Exception {

        // given
        mockMvc.perform(post(V_1_POSTS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entryPoint"));
    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ???????????? ?????? ????????????")
    void savePost_fail_parameter() throws Exception {
        // given
        PostDto.Request request = getRequest("", "", "free", "??????");
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
        assertThat(tagRepository.findByName("??????")).isEmpty();

    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ???????????? ?????? ????????? ??????")
    void savePost_fail_type() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????", "????????? ?????????", "frees", "??????");

        // when
        PostErrorCode err = PostErrorCode.NOT_EXIST_TYPE;

        mockMvc.perform(post(V_1_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

        //then
        assertThat(postRepository.existsByTitle(request.getTitle())).isFalse();
        assertThat(tagRepository.findByName("??????")).isEmpty();

    }

    @Test
    @DisplayName("[??????] ?????? ????????? ??????")
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
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? ????????? ??????")
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
    @WithAccount("?????????")
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? ????????? ??????")
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
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? ????????? ?????? X")
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
    @DisplayName("[??????] ?????? ????????? ?????? - ???????????? ?????? ????????? ID")
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
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ??????")
    void updatePost_success() throws Exception {
        // given
        post = createPost(userRepository.findByNickname("?????????").get());
        PostDto.Request request = getRequest("?????????2", "?????????2", "free", "?????????1","??????1");

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
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ?????? ?????????????????? ?????????")
    void updatePost_success_important() throws Exception {
        // given
        post = createPost(userRepository.findByNickname("?????????").get());
        PostDto.Request request = getRequest("?????????2", "?????????2", "free",true, "?????????1","??????1");

        // when
        ResultActions perform = mockMvc.perform(put(V_1_POSTS + "/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        assertThat(post.isImportant()).isTrue();
    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ????????? ????????????")
    void updatePost_fail() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????2", "?????????2", "free", "?????????1","??????1");
        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

        // when
        ResultActions perform = mockMvc.perform(put(V_1_POSTS + "/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ??????")
    void deletePost_success() throws Exception {
        // given
        post = createPost(userRepository.findByNickname("?????????").get());

        // when

        mockMvc.perform(delete(V_1_POSTS + "/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        //then
        assertThat(postRepository.existsById(post.getId())).isFalse();

    }

    private Post createPost(User user) {
        return postFactory.createPost(user, getRequest("test", "test?????????", "free", "?????????", "??????2"));
    }

    @Test
    @WithAccount("?????????")
    @DisplayName("[??????] ????????? ?????? - ????????? ???????????? ????????????")
    void deletePost_fail() throws Exception {
        // given
        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

        // when

        mockMvc.perform(delete(V_1_POSTS + "/" + post.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()));

        //then
        assertThat(postRepository.existsById(post.getId())).isTrue();

    }


    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? / ?????? ???????????? ????????? ???????????? ?????????")
    void findAllPost_Success_01() throws Exception {
        // given
        for (int i = 0; i < 8; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.",FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", QUESTIONS));
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
                .andExpect(jsonPath("$.list[*].responseInfo.important").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.viewCount").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(postRepository.countByType(FREE))));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? / ?????? ???????????? ????????? ???????????? ?????????")
    void findAllPost_Success_02() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.",FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", QUESTIONS));
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
                .andExpect(jsonPath("$.list[*].responseInfo.important").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.nickname").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.important").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.profileImage").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.likeCount").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.viewCount").exists())
                .andExpect(jsonPath("$.list[*]", hasSize(Integer.valueOf(limit))));
    }


    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? / ?????? ???????????? ???????????? ????????????")
    void findAllPost_Success_03() throws Exception {
        // given
        for (int i = 0; i < 3; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", PostType.NOTICE.getType(),true,"??????1","??????2"));
        }
        for (int i = 0; i < 10; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.",PostType.FREE.getType()));
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
                .andExpect(jsonPath("$.list[*].responseInfo.important").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.list[*].responseInfo.viewCount").exists());
    }


    private PostDto.Request getRequest(String title, String content, String type, String... tagName) {
        return getRequest(title, content, type, false, tagName);
    }

    private PostDto.Request getRequest(String title, String content, String type, boolean important , String... tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .type(type)
                .content(content)
                .important(important)
                .tags(Arrays.stream(tagName).map(name ->TagDto.builder().name(name).build()).collect(Collectors.toList()))
                .build();
    }
}