package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.WithAccount;
import com.importH.core.domain.banner.Banner;
import com.importH.core.domain.banner.BannerRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.banner.BannerDto;
import com.importH.core.dto.banner.BannerDto.Request;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.BannerErrorCode;
import com.importH.core.service.BannerService;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:/application-test.properties")
class BannerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    BannerService bannerService;

    @Autowired
    BannerRepository bannerRepository;


    @Test
    @WithAccount("관리자")
    @DisplayName("[성공] 배너 등록")
    void registerBanner_success() throws Exception {
        // given
        Request request = getRequest();

        // when
        ResultActions perform = mockMvc.perform(post("/v1/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        Banner banner = bannerRepository.findByTitle(getRequest().getTitle()).get();

        assertThat(banner).isNotNull();

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bannerId").value(banner.getId()))
                .andExpect(jsonPath("$.data.title").value(banner.getTitle()))
                .andExpect(jsonPath("$.data.url").value(banner.getUrl()))
                .andExpect(jsonPath("$.data.imgUrl").value(banner.getImageUrl()))
                .andExpect(jsonPath("$.data.content").value(banner.getContent()))
                .andExpect(jsonPath("$.data.tags[*].name").value(banner.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 배너 등록 - 권한이 없는 유저")
    void registerBanner_fail() throws Exception {
        // given
        Request request = getRequest();
        BannerErrorCode errorCode = BannerErrorCode.NOT_AUTHORITY_REGISTER;

        // when
        ResultActions perform = mockMvc.perform(post("/v1/banners")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value(errorCode.getDescription()))
                .andDo(print());

    }

    @Test
    @DisplayName("[성공] 배너 가져오기")
    void getBanners_success() throws Exception {

        // given
        for (int i = 0; i < 3; i++) {
            Request request = getRequest(i);
            bannerService.registerBanner(request, "ROLE_ADMIN");
        }
        // when
        ResultActions perform = mockMvc.perform(get("/v1/banners"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list[*].bannerId").exists())
                .andExpect(jsonPath("$.list[*].title").exists())
                .andExpect(jsonPath("$.list[*].url").exists())
                .andExpect(jsonPath("$.list[*].imgUrl").exists())
                .andExpect(jsonPath("$.list[*].content").exists())
                .andExpect(jsonPath("$.list[*].tags").exists())
                .andDo(print());

    }

    private Request getRequest() {
        return getRequest(1);
    }

    private Request getRequest(int seq) {
        Request bannerDto = Request.builder()
                .title("제목"+seq)
                .url("http://cafe.naver.com")
                .tags(List.of(getTag("태그1"), getTag("태그2")))
                .content("배너 내용"+seq)
                .imgUrl("배너 이미지 주소")
                .build();
        return bannerDto;
    }

    private TagDto getTag(String name) {
        return TagDto.builder().name(name).build();
    }



}