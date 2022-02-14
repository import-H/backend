package com.importH.core.dto.banner;

import com.importH.core.domain.banner.Banner;
import com.importH.core.dto.tag.TagDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


public class BannerDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ApiModel(value = "배너 요청 DTO")
    public static class Request {

        @Length(max = 100,message = "100자 이내로 입력해 주세요")
        @NotEmpty(message = "배너 제목은 필수값 입니다.")
        @ApiModelProperty(value = "배너 제목", example = "테스트 배너")
        private String title;

        @ApiModelProperty(value = "배너 작성자", example = "닉네임 ")
        @NotEmpty(message = "배너 작성자는 필수값 입니다.")
        private String  nickname;

        @NotEmpty(message = "url 은 필수 값 입니다.")
        @URL(message = "url 형태로 입력해 주세요.")
        @ApiModelProperty(value = "배너 누를시 링크", example = "http://...")
        private String url;

        @NotEmpty(message = "배너 이미지는 필수값 입니다.")
        @ApiModelProperty(value = "배너 이미지 저장 주소", example = "http://.../v1/file/upload/...")
        private String imgUrl;

        @NotEmpty(message = "배너 내용은 필수값 입니다.")
        @ApiModelProperty(value = "배너 내용", example = "배너 입니다.")
        private String content;


        @ApiModelProperty(value = "배너 태그 정보")
        private List<TagDto> tags;

        public Banner toEntity() {
            return Banner.builder()
                    .nickname(nickname)
                    .imageUrl(imgUrl)
                    .content(content)
                    .title(title)
                    .url(url)
                    .tags(new HashSet<>())
                    .build();
        }
    }


    @Getter
    @Builder
    @ApiModel(value = "배너 응답 DTO")
    public static class Response {

        @ApiModelProperty(value = "배너 ID", example = "1")
        private Long bannerId;

        @ApiModelProperty(value = "배너 작성자", example = "닉네임 ")
        private String  nickname;

        @ApiModelProperty(value = "배너 제목", example = "테스트 배너")
        private String title;

        @ApiModelProperty(value = "배너 누를시 링크", example = "http://...")
        private String url;

        @ApiModelProperty(value = "배너 이미지 저장 주소", example = "http://.../v1/file/upload/...")
        private String imgUrl;

        @ApiModelProperty(value = "배너 내용", example = "배너 입니다.")
        private String content;

        @ApiModelProperty(value = "배너 태그 정보")
        private List<TagDto> tags;

        public static Response fromEntity(Banner banner) {
            return Response.builder()
                    .bannerId(banner.getId())
                    .title(banner.getTitle())
                    .nickname(banner.getNickname())
                    .content(banner.getContent())
                    .imgUrl(banner.getImageUrl())
                    .url(banner.getUrl())
                    .tags(banner.getTags().stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toList()))
                    .build();
        }
    }
}
