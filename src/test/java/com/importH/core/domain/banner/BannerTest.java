package com.importH.core.domain.banner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class BannerTest {

    @Test
    @DisplayName("배너 이미지 주소 테스트")
    void getImgUrl() throws Exception {
        Banner banner = Banner.builder().imageUrl("/v1/file/upload/41e7688b-026a-4261-87c4-4053fb6c496e.png").build();

        System.out.println(banner.getStoreImageUrl());
        assertEquals(banner.getStoreImageUrl(),"41e7688b-026a-4261-87c4-4053fb6c496e.png");
    }
}