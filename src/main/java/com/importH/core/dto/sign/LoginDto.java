package com.importH.core.dto.sign;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "로그인 DTO")
public class LoginDto {


    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ApiModel(value = "로그인 요청 DTO")
    public static class Request {

        @ApiModelProperty(value = "이메일", example = "test@hongik.ac.kr")
        @NotBlank(message = "이메일은 필수 입력값 입니다.")
        private String email;

        @ApiModelProperty(value = "게시글 제목", example = "테스트 게시글")
        @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
        private String password;
    }

    @Getter
    @Builder
    @ApiModel(value = "로그인 응답 DTO")
    public static class Response {


        private String accessToken;

        private String refreshToken;

        @ApiModelProperty(value = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(value = "프로필 이미지 주소", example = "http://localhost:8090/v1/profile/...")
        private String profileImage;

    }
}
