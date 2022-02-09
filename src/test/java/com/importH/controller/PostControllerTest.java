package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.core.domain.account.Account;
import com.importH.core.domain.account.AccountRepository;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.domain.tag.TagRepository;
import com.importH.core.dto.post.CommentDto;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.service.PostService;
import com.importH.core.service.TagService;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostControllerTest {

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
    AccountRepository accountRepository;

    Post post;

    @BeforeEach
    void before() {
        Account account = accountRepository.findByNickname("test").get();
        post = postFactory.createPost(account, 1, getRequest("test", "test게시글", "스터디","자바2"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 등록")
    void savePost_success() throws Exception {
        // given

        PostDto.Request request = getRequest("테스트", "테스트 게시글", "자바");

        // when
        mockMvc.perform(post("/v1/boards/1/posts")
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
        mockMvc.perform(post("/v1/boards/1/posts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entryPoint"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 게시글 등록 - 유효하지 않은 파라미터")
    void savePost_fail_parameter() throws Exception {
        // given
        PostDto.Request request = getRequest("", "", "자바");
        // when
        PostErrorCode errorCode = PostErrorCode.NOT_VALIDATE_PARAM;

        mockMvc.perform(post("/v1/boards/1/posts")
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
        ResultActions perform = mockMvc.perform(get("/v1/boards/1/posts/" + post.getId()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.responseInfo.id").value(post.getId()))
                .andExpect(jsonPath("$.data.responseInfo.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.responseInfo.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.responseInfo.user.nickname").value(post.getAccount().getNickname()))
                .andExpect(jsonPath("$.data.responseInfo.user.profileImage").value(post.getAccount().getProfileImage()))
                .andExpect(jsonPath("$.data.responseInfo.likeCount").value(post.getLikeCount()))
                .andExpect(jsonPath("$.data.responseInfo.tags[*].name").exists())
                .andExpect(jsonPath("$.data.responseInfo.viewCount").value(post.getViewCount()))
                .andExpect(jsonPath("$.data.like").value(false))
                .andExpect(jsonPath("$.data.comments[*]").value(postService.getCommentDtos(post)));

    }

    @Test
    @DisplayName("[실패] 단일 게시글 조회 - 옳바르지 않은 게시글 ID")
    void findPost_fail() throws Exception {
        //given
        PostErrorCode notFoundPost = PostErrorCode.NOT_FOUND_POST;

        // when
        ResultActions perform = mockMvc.perform(get("/v1/boards/1/posts/2"));


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
        post = postFactory.createPost(accountRepository.findByNickname("테스트").get(), 1, getRequest("test", "test게시글", "스터디", "자바2"));
        PostDto.Request request = getRequest("테스트2", "테스트2", "스터디1","자바1");

        // when
        ResultActions perform = mockMvc.perform(put("/v1/boards/1/posts/" + post.getId())
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
        PostDto.Request request = getRequest("테스트2", "테스트2", "스터디1","자바1");
        PostErrorCode notAccordAccount = PostErrorCode.NOT_ACCORD_ACCOUNT;

        // when
        ResultActions perform = mockMvc.perform(put("/v1/boards/1/posts/" + post.getId())
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
        post = postFactory.createPost(accountRepository.findByNickname("테스트").get(), 1, getRequest("test", "test게시글", "스터디", "자바2"));

        // when

        mockMvc.perform(delete("/v1/boards/1/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").exists());

        //then
        assertThat(postRepository.existsById(post.getId())).isFalse();

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 게시글 삭제 - 게시글 작성자가 아닌경우")
    void deletePost_fail() throws Exception {
        // given
        PostErrorCode notAccordAccount = PostErrorCode.NOT_ACCORD_ACCOUNT;

        // when

        mockMvc.perform(delete("/v1/boards/1/posts/" + post.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(notAccordAccount.getDescription()));

        //then
        assertThat(postRepository.existsById(post.getId())).isTrue();

    }


    // TODO 전체게시글 조회 테스트



    private PostDto.Request getRequest(String title, String content, String... tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .content(content)
                .tags(Arrays.stream(tagName).map(name ->TagDto.builder().name(name).build()).collect(Collectors.toList()))
                .build();
    }
}