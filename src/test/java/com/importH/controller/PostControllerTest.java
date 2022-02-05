package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.WithAccount;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.tag.TagRepository;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.JwtErrorCode;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.service.PostService;
import com.importH.core.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

    @Test
    @WithAccount("test")
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
    @WithAccount("test")
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

    private PostDto.Request getRequest(String title, String content, String tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .content(content)
                .tags(List.of(TagDto.builder()
                        .name(tagName)
                        .build()))
                .build();
    }
}