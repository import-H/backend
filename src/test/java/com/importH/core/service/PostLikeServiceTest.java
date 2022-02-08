package com.importH.core.service;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.core.domain.account.Account;
import com.importH.core.domain.account.AccountRepository;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostLikeRepository;
import com.importH.core.domain.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostLikeServiceTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostLikeService postLikeService;

    @Autowired
    PostFactory postFactory;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    Account account;
    Post post;

    @BeforeEach
    void before() {
        account = accountRepository.findByNickname("테스트").get();
        post = createPost();
    }

    private Post createPost() {
        return postFactory.createPost(account, 1, postFactory.getRequest("test", "test", "테스트"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요 요청 좋아요수 증가")
    void increasePostLike() throws Exception {

        // when
        postLikeService.changeLike(account,post.getId());

        //then
        assertThat(postLikeRepository.existsByAccountAndPost(account, post)).isTrue();
        assertThat(post.getLikeCount()).isEqualTo(1);
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요후 다시 좋아요 눌러서 취소")
    void decreasePostLike() throws Exception {

        //given
        postLikeService.changeLike(account,post.getId());

        // when
        postLikeService.changeLike(account,post.getId());


        //then
        assertThat(postLikeRepository.existsByAccountAndPost(account, post)).isFalse();
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 삭제시 해당 게시글 좋아요 데이터 삭제")
    void deletePostWithPostLike() throws Exception {
        // given
        postLikeService.changeLike(account,post.getId());

        // when
        postRepository.delete(post);

        //then
        assertThat(postLikeRepository.existsByPostId(post.getId())).isFalse();
    }
}