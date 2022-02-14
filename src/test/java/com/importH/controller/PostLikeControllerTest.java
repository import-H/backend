package com.importH.controller;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostLikeRepository;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.service.PostLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostLikeControllerTest {

    @Autowired
    PostLikeService postLikeService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostFactory postFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    Post post;

    User user;

    @BeforeEach
    void before() {
        user = userRepository.findByNickname("테스트").get();
        post = postFactory.createPost(user, postFactory.getRequest("test", "test게시글", "스터디","자바2"));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요")
    void increaseLike() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(post("/v1/posts/" + post.getId() + "/like"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertEquals(post.getLikeCount(),1);
        assertEquals(postLikeRepository.existsByUserAndPost(user,post),true);
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 좋아요 취소")
    void decreaseLike() throws Exception {
        //given
        postLikeService.changeLike(user, post.getId());

        // when
        ResultActions perform = mockMvc.perform(post("/v1/posts/" + post.getId() + "/like"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertEquals(post.getLikeCount(),0);
        assertEquals(postLikeRepository.existsByUserAndPost(user,post),false);
    }
}