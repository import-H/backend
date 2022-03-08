package com.importH.domain.banner;

import com.importH.domain.banner.BannerDto.Request;
import com.importH.domain.banner.BannerDto.Response;
import com.importH.domain.image.FileService;
import com.importH.domain.tag.TagService;
import com.importH.global.error.exception.BannerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.importH.global.error.code.BannerErrorCode.NOT_FOUND_BANNER;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final FileService fileService;
    private final TagService tagService;


    /**
     * 배너 등록
     */
    @Transactional
    public Response registerBanner(Request bannerDto) {

        Banner banner = bannerDto.toEntity();
        banner.setTags(tagService.getTags(bannerDto.getTags()));


        return Response.fromEntity(saveBanner(banner));
    }

    private Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }



    public Banner findById(Long bannerId) {
        return bannerRepository.findById(bannerId).orElseThrow(() -> new BannerException(NOT_FOUND_BANNER));
    }

    public List<Response> getBanners() {
        List<Banner> banners = bannerRepository.findAll();

        return banners.stream().map(banner -> Response.fromEntity(banner))
                .collect(Collectors.toList());
    }

    /**
     * 배너 삭제
     */
    @Transactional
    public void deleteBanner(Long bannerId) {

        Banner banner = findById(bannerId);
        fileService.deleteImage(banner.getStoreImageUrl());
        bannerRepository.delete(banner);
    }
}
