package com.importH.domain.post.controller;

import com.importH.core.PostFactory;
import com.importH.core.WithAccount;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostScrap;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.post.repository.PostScrapRepository;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.PostErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostScrapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @MockBean
    PostScrapRepository postScrapRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostFactory postFactory;

    Post post;
    User user;

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
    @DisplayName("[성공] 게시글 스크랩 - 정상적인 요청")
    void scrap_success() throws Exception {
        // given
        given(postScrapRepository.save(any())).willReturn(PostScrap.create(post,user));

        // when
        ResultActions perform = mockMvc.perform(post("/v1/posts/" + post.getId() + "/scrap"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(post.getScraps().size()).isEqualTo(1);
        verify(postScrapRepository, times(1)).save(any());
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 게시글 스크랩 - 옳바르지 않은 게시글")
    void scrap_fail() throws Exception {
        // given
        given(postScrapRepository.save(any())).willReturn(PostScrap.create(post,user));
        PostErrorCode err = PostErrorCode.NOT_FOUND_POST;

        // when
        ResultActions perform = mockMvc.perform(post("/v1/posts/" + 999 + "/scrap"));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(err.getDescription()));

        assertThat(post.getScraps().size()).isEqualTo(0);
        verify(postScrapRepository, never()).save(any());
    }


    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 스크랩 취소 - 정상적인 요청")
    void scrapCancel_success() throws Exception {
        // given
        post.addScrap(PostScrap.create(post,user));

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/posts/" + post.getId() + "/scrap"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(post.getScraps().size()).isEqualTo(0);
        verify(postScrapRepository, never()).save(any());
        verify(postScrapRepository, times(1)).delete(any());
    }
}