package com.importH.core.service;

import com.importH.core.domain.banner.Banner;
import com.importH.core.domain.banner.BannerRepository;
import com.importH.core.dto.banner.BannerDto.Request;
import com.importH.core.dto.banner.BannerDto.Response;
import com.importH.core.error.exception.BannerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.importH.core.error.code.BannerErrorCode.NOT_AUTHORITY_REGISTER;
import static com.importH.core.error.code.BannerErrorCode.NOT_FOUND_BANNER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    private final TagService tagService;


    @Transactional
    public Response registerBanner(Request bannerDto, String role) {

        isAdmin(role);
        Banner banner = bannerDto.toEntity();
        banner.setTags(tagService.getTags(bannerDto.getTags()));


        return Response.fromEntity(saveBanner(banner));
    }

    private Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    private void isAdmin(String role) {
        if (!role.equals("ROLE_ADMIN")) {
            throw new BannerException(NOT_AUTHORITY_REGISTER);
        }
    }

    public Banner findById(Long bannerId) {
        return bannerRepository.findById(bannerId).orElseThrow(() -> new BannerException(NOT_FOUND_BANNER));
    }

    public Response getBanner(Long bannerId) {
        Banner banner = findById(bannerId);

        return Response.fromEntity(banner);
    }

    public void deleteBanner(Banner banner, String role) {

        isAdmin(role);
        bannerRepository.delete(banner);
    }
}
