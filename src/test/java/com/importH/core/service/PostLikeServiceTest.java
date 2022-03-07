package com.importH.core.service;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.repository.PostLikeRepository;
import com.importH.domain.post.service.PostLikeService;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostLikeServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostLikeService postLikeService;

    @Autowired
    PostFactory postFactory;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    User user;
    Post post;

    @BeforeEach
    void before() {
        user = userRepository.findByNickname("테스트").get();
        post = createPost();
    }

    private Post createPost() {
        return postFactory.createPost(user, postFactory.getRequest("test", "test", "테스트"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요 요청 좋아요수 증가")
    void increasePostLike() throws Exception {

        // when
        postLikeService.changeLike(user,post.getId());

        //then
        assertThat(postLikeRepository.existsByUserAndPost(user, post)).isTrue();
        assertThat(post.getLikeCount()).isEqualTo(1);
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요후 다시 좋아요 눌러서 취소")
    void decreasePostLike() throws Exception {

        //given
        postLikeService.changeLike(user,post.getId());

        // when
        postLikeService.changeLike(user,post.getId());


        //then
        assertThat(postLikeRepository.existsByUserAndPost(user, post)).isFalse();
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 삭제시 해당 게시글 좋아요 데이터 삭제")
    void deletePostWithPostLike() throws Exception {
        // given
        postLikeService.changeLike(user,post.getId());

        // when
        postRepository.delete(post);

        //then
        assertThat(postLikeRepository.existsByPostId(post.getId())).isFalse();
    }
}