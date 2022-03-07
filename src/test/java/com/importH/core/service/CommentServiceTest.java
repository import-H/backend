package com.importH.core.service;

import com.importH.domain.comment.Comment;
import com.importH.domain.comment.CommentDto.Request;
import com.importH.domain.comment.CommentRepository;
import com.importH.domain.comment.CommentService;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.service.PostService;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.CommentErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.exception.CommentException;
import com.importH.global.error.exception.PostException;
import com.importH.global.error.exception.SecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static com.importH.global.error.code.PostErrorCode.NOT_FOUND_POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    @Mock
    PostService postService;

    @Spy
    ApplicationEventPublisher publisher;

    Post post;
    User user;
    Request request;

    @BeforeEach
    void init() {
         post = getPost();
         user = getUser(5L);
        request = getRequest("댓글 입니다.2");
    }
    @Test
    @DisplayName("[성공] 댓글 등록")
    void regComment_success() throws Exception {

        // given
        given(postService.findByPostId(any())).willReturn(post);

        // when
        commentService.registerComment(1L, user, request);

        //then
        assertThat(post.getComments().size()).isEqualTo(1);

        Comment comment = post.getComments().stream().findFirst().get();
        assertThat(comment)
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("content",request.getContent())
                .hasFieldOrPropertyWithValue("post",post)
                .hasFieldOrPropertyWithValue("user",user);

        verify(commentRepository, times(1)).save(any());
    }





    @Test
    @DisplayName("[실패] 댓글 등록 - 존재하지 않는 게시글")
    void regComment_fail() throws Exception {

        // when
        when(postService.findByPostId(any())).thenThrow(new PostException(NOT_FOUND_POST));

        //then
        assertThrows(PostException.class, () -> commentService.registerComment(post.getId(), user, request));

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("[성공] 댓글 수정 ")
    void updateComment_success() throws Exception {
        // given
        Request updateRequest = getRequest("댓글 입니다.2");
        Comment comment = getComment(request,post,user);

        given(postService.findByPostId(any())).willReturn(post);
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));

        // when
        commentService.updateComment(post.getId(), comment.getId(), user, updateRequest);

        //then
        assertThat(comment)
                .hasFieldOrPropertyWithValue("content", updateRequest.getContent());

        verify(commentRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("[실패] 댓글 수정 - 동일하지 않은 게시글")
    void updateComment_fail_NotEqualsPost() throws Exception {
        // given
        Request updateRequest = getRequest("댓글 입니다.2");
        Comment comment = getComment(request,post,user);
        CommentErrorCode err = CommentErrorCode.NOT_EQUALS_POST;

        given(postService.findByPostId(any())).willReturn(getPost());
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));

        // when
        CommentException exception = assertThrows(CommentException.class, () -> commentService.updateComment(post.getId(), comment.getId(), user, updateRequest));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage",err.getDescription());

    }

    @Test
    @DisplayName("[실패] 댓글 수정 - 동일하지 않은 유저")
    void updateComment_fail_NotEqualsUser() throws Exception {
        // given
        Request updateRequest = getRequest("댓글 입니다.2");
        Comment comment = getComment(request,post,user);
        User another = getUser(10L);
        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

        given(postService.findByPostId(any())).willReturn(post);
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));

        // when
        SecurityException exception = assertThrows(SecurityException.class, () -> commentService.updateComment(post.getId(), comment.getId(), another, updateRequest));

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage",errorCode.getDescription());

    }


    @Test
    @DisplayName("[성공] 댓글 삭제")
    void deleteComment_fail() throws Exception {
        // given
        Comment comment = getComment(request,post,user);

        given(postService.findByPostId(any())).willReturn(post);
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(post.getId(),comment.getId(),user);

        //then
        verify(commentRepository, times(1)).delete(any());
    }


    @Test
    @DisplayName("[실패] 댓글 삭제 - 동일하지 않은 유저")
    void deleteComment_success() throws Exception {
        // given
        Comment comment = getComment(request,post,user);
        User user = getUser(10L);

        given(postService.findByPostId(any())).willReturn(post);
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));

        SecurityErrorCode errorCode = SecurityErrorCode.ACCESS_DENIED;

        // when
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            commentService.deleteComment(post.getId(), comment.getId(), user);
        });

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage",errorCode.getDescription());


        verify(commentRepository, never()).delete(any());
    }

    private Comment getComment(Request request,Post post ,User user) {
        return Comment.builder()
                .id(2L)
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();
    }
    private Request getRequest(String content) {
        return Request.builder()
                .content(content)
                .build();
    }
    private Post getPost() {
        return Post.builder().id(1L).title("테스트게시글").user(User.builder().id(100L).build()).content("테스트").build();
    }

    private User getUser(long id) {
        return User.builder().id(id).nickname("테스트").password("1234").email("테스트").build();
    }


}