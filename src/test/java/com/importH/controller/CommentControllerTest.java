package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.comment.Comment;
import com.importH.domain.comment.CommentDto.Request;
import com.importH.domain.comment.CommentRepository;
import com.importH.domain.comment.CommentService;
import com.importH.domain.post.Post;
import com.importH.domain.post.PostRepository;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.CommentErrorCode;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostFactory postFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    Post post;

    User user;

    @BeforeEach
    void before() {
        user = userRepository.findByNickname("테스트").get();
        post = postFactory.createPost(user, postFactory.getRequest("test", "test게시글", "스터디","자바2"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 댓글 등록")
    void regComment_success() throws Exception {

        // given
        Request request = getRequest("테스트 댓글");

        // when
        ResultActions perform = mockMvc.perform(post("/v1/posts/" + post.getId() + "/comments")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(commentRepository.count()).isEqualTo(1);

        Comment comment = commentRepository.findAll().get(0);
        assertThat(post.getComments()).contains(comment);
        assertThat(comment)
                .hasFieldOrPropertyWithValue("content", request.getContent())
                .hasFieldOrPropertyWithValue("post", post)
                .hasFieldOrPropertyWithValue("user", user);

    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 댓글 등록 - postId 가 옳바르지 않은 경우")
    void regComment_fail_postId() throws Exception {

        // given
        Request request = getRequest("테스트 댓글");
        PostErrorCode err = PostErrorCode.NOT_FOUND_POST;

        // when
        ResultActions perform = mockMvc.perform(post("/v1/posts/1/comments")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

        assertThat(commentRepository.count()).isEqualTo(0);

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 댓글 수정")
    void updateComment_success() throws Exception {

        // given
        Long commentId = commentService.registerComment(post.getId(), user, getRequest("테스트 댓글"));
        Request request = getRequest("테스트 댓글 2");
        Comment comment = commentRepository.findById(commentId).get();

        // when
        ResultActions perform = mockMvc.perform(put("/v1/posts/" + post.getId() + "/comments/" + commentId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));


        assertThat(comment)
                .hasFieldOrPropertyWithValue("content", request.getContent());
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 댓글 수정 - 댓글 작성자 본인이 아닌경우")
    void updateComment_fail_NotEqualsUser() throws Exception {

        // given
        User another = userRepository.findByNickname("test1").get();
        Long commentId = commentService.registerComment(post.getId(), another, getRequest("테스트 댓글"));
        Request request = getRequest("테스트 댓글 2");
        Comment comment = commentRepository.findById(commentId).get();
        CommentErrorCode err = CommentErrorCode.NOT_EQUALS_USER;

        // when
        ResultActions perform = mockMvc.perform(put("/v1/posts/" + post.getId() + "/comments/" + commentId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));


        assertThat(comment.getContent())
                .isNotEqualTo(request);

    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 댓글 수정 - 게시글이 동일하지 않은경우")
    void updateComment_fail_NotEqualsPost() throws Exception {

        // given
        Long commentId = commentService.registerComment(post.getId(), user, getRequest("테스트 댓글"));

        Request request = getRequest("테스트 댓글 2");
        Comment comment = commentRepository.findById(commentId).get();

        Post another = postRepository.findByTitle("test0");
        CommentErrorCode err = CommentErrorCode.NOT_EQUALS_POST;

        // when
        ResultActions perform = mockMvc.perform(put("/v1/posts/" + another.getId() + "/comments/" + commentId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));


        assertThat(comment.getContent())
                .isNotEqualTo(request);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 댓글 삭제")
    void deleteComment_success() throws Exception {

        // given
        Long commentId = commentService.registerComment(post.getId(), user, getRequest("테스트 댓글"));

        assertThat(commentRepository.existsById(commentId)).isTrue();
        assertThat(post.getComments().size()).isEqualTo(1);

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/posts/" + post.getId() + "/comments/" + commentId));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(commentRepository.existsById(commentId)).isFalse();
        assertThat(post.getComments().size()).isEqualTo(0);
    }

    private Request getRequest(String content) {
        return Request.builder()
                .content(content)
                .build();
    }
}