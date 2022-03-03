package com.importH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.WithAccount;
import com.importH.domain.banner.Banner;
import com.importH.domain.banner.BannerDto.Request;
import com.importH.domain.banner.BannerDto.Response;
import com.importH.domain.banner.BannerRepository;
import com.importH.domain.banner.BannerService;
import com.importH.domain.tag.Tag;
import com.importH.domain.tag.TagDto;
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

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        // when
        ResultActions perform = mockMvc.perform(post("/v1/banners")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().is3xxRedirection());
        assertThat(bannerRepository.findByTitle(request.getTitle())).isEmpty();

    }

    @Test
    @DisplayName("[성공] 배너 가져오기")
    void getBanners_success() throws Exception {

        // given
        for (int i = 0; i < 3; i++) {
            Request request = getRequest(i);
            bannerService.registerBanner(request);
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

    @Test
    @WithAccount("관리자")
    @DisplayName("[성공] 관리자가 존재하는 배너 삭제하기")
    void deleteBanner_success() throws Exception {
        // given
        Response response = bannerService.registerBanner(getRequest());
        Banner banner = bannerRepository.findById(response.getBannerId()).get();
        File file = new File(banner.getImageUrl());

        // when
        ResultActions perform = mockMvc.perform(delete("/v1/banners/" + banner.getId()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(bannerRepository.findById(banner.getId())).isEmpty();

        assertThat(file.exists()).isFalse();
    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[실패] 배너 삭제하기 - 권한이 없는 유저가 접근")
    void deleteBanner_fail_user() throws Exception {
        // given
        Response response = bannerService.registerBanner(getRequest());
        Banner banner = bannerRepository.findById(response.getBannerId()).get();
        // when
        ResultActions perform = mockMvc.perform(delete("/v1/banners/" + banner.getId()));

        //then
        perform.andExpect(status().is3xxRedirection());

        assertThat(bannerRepository.findById(banner.getId())).isPresent();

    }




    private Request getRequest() {
        return getRequest(1);
    }

    private Request getRequest(int seq) {
        Request bannerDto = Request.builder()
                .title("제목"+seq)
                .nickname("관리자")
                .url("http://cafe.naver.com")
                .tags(List.of(getTag("태그1"), getTag("태그2")))
                .content("배너 내용"+seq)
                .imgUrl("95d9da62-a6c6-43df-8caa-bb80a3d71b1d.png")
                .build();
        return bannerDto;
    }

    private TagDto getTag(String name) {
        return TagDto.builder().name(name).build();
    }



}