package com.importH.domain.post.service;

import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostScrap;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.post.repository.PostScrapRepository;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.exception.PostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostScrapServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    PostScrapRepository postScrapRepository;

    @InjectMocks
    PostScrapService postScrapService;

    @Test
    @DisplayName("[성공] 게시글 스크랩하기 - 다른 사람 게시글")
    void scrap_success() throws Exception {
        // given
        User user = getUser(2L, "test01@mail.com", "test2");
        User postAuthor = getUser(1L, "test@mail.com", "test");
        Post post = getPost(postAuthor);
        given(postRepository.findPostWithScrapById(any())).willReturn(Optional.of(post));

        // when
        postScrapService.scrap(post.getId(), user);

        //then
        verify(postRepository, times(1)).findPostWithScrapById(any());
        verify(postScrapRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("[성공] 게시글 스크랩하기 - 본인 게시글")
    void scrap_success_author() throws Exception {
        // given
        User postAuthor = getUser(1L, "test@mail.com", "test");
        Post post = getPost(postAuthor);
        given(postRepository.findPostWithScrapById(any())).willReturn(Optional.of(post));

        // when
        postScrapService.scrap(post.getId(), postAuthor);

        //then
        verify(postRepository, times(1)).findPostWithScrapById(any());
        verify(postScrapRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("[실패] 게시글 스크랩하기 - 존재하지 않는 게시글")
    void scrap_fail() throws Exception {
        // given
        User user = getUser(2L, "test01@mail.com", "test2");
        User postAuthor = getUser(1L, "test@mail.com", "test");
        Post post = getPost(postAuthor);
        PostErrorCode err = PostErrorCode.NOT_FOUND_POST;
        // when
        PostException exception = assertThrows(PostException.class, () -> postScrapService.scrap(post.getId(), user));

        //then
        assertThat(exception).hasMessageContaining(err.getDescription());
        verify(postRepository, times(1)).findPostWithScrapById(any());
        verify(postScrapRepository, never()).save(any());
    }


    @Test
    @DisplayName("[성공] 게시글 스크랩 취소하기")
    void scrapCancel_success() throws Exception {
        // given
        User user = getUser(2L, "test01@mail.com", "test2");
        User postAuthor = getUser(1L, "test@mail.com", "test");
        Post post = getPost(postAuthor);
        post.addScrap(PostScrap.create(post, user));

        given(postRepository.findPostWithScrapById(any())).willReturn(Optional.of(post));

        // when
        postScrapService.cancelScrap(post.getId(), user);

        //then
        assertThat(post.getScraps()).hasSize(0);
        verify(postRepository, times(1)).findPostWithScrapById(any());
        verify(postScrapRepository, never()).save(any());
    }


    private Post getPost(User postAuthor) {
        return Post.builder().id(100L).title("테스트 게시글").content("테스트 게시글 입니다.").user(postAuthor).build();
    }

    private User getUser(long id, String email, String nickname) {
        return User.builder().id(id).email(email).nickname(nickname).build();
    }


}