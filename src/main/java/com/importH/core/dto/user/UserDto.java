package com.importH.core.dto.user;

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

        @ApiModelProperty(value = "프로필 이미지 주소", example = "http://localhost:8090/v1/profile/...")
        private String profileImage;

    }
}
