package com.importH.core.service;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.repository.PostLikeRepository;
import com.importH.domain.post.service.PostLikeService;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        postLikeService.addLike(user,post.getId());

        //then
        assertThat(postLikeRepository.existsByUserAndPost(user, post)).isTrue();
        assertThat(post.getLikeCount()).isEqualTo(1);
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요후 다시 좋아요 눌러서 취소")
    void decreasePostLike() throws Exception {

        //given
        postLikeService.addLike(user,post.getId());

        // when
        postLikeService.cancelLike(user,post.getId());


        //then
        assertThat(postLikeRepository.existsByUserAndPost(user, post)).isFalse();
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 삭제시 해당 게시글 좋아요 데이터 삭제")
    void deletePostWithPostLike() throws Exception {
        // given
        postLikeService.addLike(user,post.getId());

        // when
        postRepository.delete(post);

        //then
        assertThat(postLikeRepository.existsByPostId(post.getId())).isFalse();
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("인가된 유저가 좋아요 한 게시글 가져오기 ")
    void findAllPostByLike_success() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            Post post = createPost();
            postLikeService.addLike(user,post.getId());
        }
        PageRequest of = PageRequest.of(0, 10);
        // when

        List<UserPostDto.Response> allPostLike = postLikeService.findAllPostLike(user, of);

        //then
        assertThat(allPostLike).hasSize(10);
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("인가된 유저가 좋아요 한 5개 게시글 가져오기 ")
    void findAllPost5ByLike_success() throws Exception {
        // given
        for (int i = 0; i < 5; i++) {
            Post post = createPost();
            postLikeService.addLike(user,post.getId());
        }
        for (int i = 0; i < 5; i++) {
            createPost();
        }

        PageRequest of = PageRequest.of(0, 10);

        // when
        List<UserPostDto.Response> allPostLike = postLikeService.findAllPostLike(user, of);

        //then
        assertThat(allPostLike)
                .hasSize(5)
                .extracting("author")
                .containsAnyOf(user.getNickname());
    }
}