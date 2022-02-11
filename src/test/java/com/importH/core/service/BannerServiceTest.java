package com.importH.core.service;

import com.importH.core.domain.banner.Banner;
import com.importH.core.domain.banner.BannerRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.banner.BannerDto.Request;
import com.importH.core.dto.banner.BannerDto.Response;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.BannerErrorCode;
import com.importH.core.error.exception.BannerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
class BannerServiceTest {

    @Mock
    BannerRepository bannerRepository;

    @Mock
    TagService tagService;

    @InjectMocks
    BannerService bannerService;


    @Test
    @DisplayName("[성공] 배너 등록")
    void registerBanner_success() throws Exception {
        // given
        Request req = getRequest();


        // when
        when(bannerRepository.save(any())).thenReturn(getEntity(req));
        when(tagService.getTags(any())).thenReturn(req.getTags().stream().map(tagDto -> Tag.builder().name(tagDto.getName()).build()).collect(Collectors.toSet()));

        Response response = bannerService.registerBanner(req,"ROLE_ADMIN");

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("title", req.getTitle())
                .hasFieldOrPropertyWithValue("content", req.getContent())
                .hasFieldOrPropertyWithValue("imgUrl", req.getImgUrl())
                .hasFieldOrPropertyWithValue("url", req.getUrl());

        assertThat(response.getTags()).hasSameElementsAs(req.getTags());



        verify(bannerRepository, times(1)).save(any());
        verify(tagService, times(1)).getTags(any());

    }

    @Test
    @DisplayName("[실패] 배너 등록 - 권한없는 사용자")
    void registerBanner_fail() throws Exception {
        // given
        Request req = getRequest();
        BannerErrorCode err = BannerErrorCode.NOT_AUTHORITY_REGISTER;

        // when
        BannerException bannerException = assertThrows(BannerException.class, () -> bannerService.registerBanner(req, "ROLE_USER"));

        //then
        assertThat(bannerException)
                .hasFieldOrPropertyWithValue("errorCode", err)
                .hasFieldOrPropertyWithValue("errorMessage",err.getDescription());

        verify(bannerRepository, never()).save(any());
    }

    private Request getRequest() {
        Request bannerDto = Request.builder()
                .title("제목")
                .url("http://cafe.naver.com")
                .tags(List.of(getTag("태그1"), getTag("태그2")))
                .content("배너 내용")
                .imgUrl("배너 이미지 주소")
                .build();
        return bannerDto;
    }


    private Banner getEntity(Request bannerDto) {
        return getEntity(bannerDto, 1L);
    }

    private Banner getEntity(Request bannerDto, long id) {

        return Banner.builder()
                .id(id)
                .title(bannerDto.getTitle())
                .tags(bannerDto.getTags().stream().map(TagDto::toEntity).collect(Collectors.toSet()))
                .content(bannerDto.getContent())
                .url(bannerDto.getUrl())
                .imageUrl(bannerDto.getImgUrl()).build();
    }

    private TagDto getTag(String name) {
        return TagDto.builder().name(name).build();
    }


}