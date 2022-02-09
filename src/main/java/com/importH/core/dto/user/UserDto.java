package com.importH.core.dto.user;

import com.importH.core.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@ApiModel(value = "유저 정보 DTO")
public class UserDto {

    @Getter
    @Builder
    @ApiModel(value = "유저 정보 응답 DTO")
    public static class Response {

        @ApiModelProperty(value = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(value = "이메일", example = "abc@hongik.ac.kr")
        private String email;

        @ApiModelProperty(value = "프로필 이미지 주소", example = "http://localhost:8090/v1/profile/...")
        private String profileImage;

        @ApiModelProperty(value = "소개글", example = "안녕하세요. xxx 입니다.")
        private String introduction;

        @ApiModelProperty(value = "개인 페이지 url", example = "http://github.com..")
        private String personalUrl;

        @ApiModelProperty(value = "이메일 수신 동의 여부", example = "true/false")
        private boolean infoByEmail;

        @ApiModelProperty(value = "웹 수신 동의 여부", example = "true/false")
        private boolean infoByWeb;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .email(user.getEmail())
                    .introduction(user.getIntroduction())
                    .personalUrl(user.getPersonalUrl())
                    .infoByEmail(user.getInfoAgree().isInfoByEmail())
                    .infoByWeb(user.getInfoAgree().isInfoByWeb())
                    .build();
        }
    }
}
