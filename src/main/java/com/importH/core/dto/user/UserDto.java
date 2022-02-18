package com.importH.core.dto.user;

import com.importH.core.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ApiModel(value = "유저 정보 DTO")
public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ApiModel(value = "프로필 수정 DTO")
    public static class Request {

        @ApiModelProperty(value = "닉네임 : 영어 또는 한글로만 3~20자로 입력해주세요", required = true, example = "닉네임")
        @NotBlank(message = "닉네임은 필수 입력 값 입니다.")
        @Pattern(regexp = "^[a-zA-zㄱ-ㅎ가-힣0-9]*$", message = "영어,한글,숫자 조합으로만 입력이 가능합니다.")
        @Length(min = 2, max = 20, message = "2글자 이상 20글자 이하로 입력 해 주세요")
        private String nickname;

        @ApiModelProperty(value = "프로필 이미지 주소", example = "http://localhost:8090/v1/profile/...")
        private String profileImage;

        @Length(max = 100, message = "100자 이내로 입력해 주세요.")
        @ApiModelProperty(value = "소개글", example = "안녕하세요. xxx 입니다.")
        private String introduction;

        @URL(message = "Url 형태로 입력해 주세요")
        @ApiModelProperty(value = "개인 페이지 url", example = "http://github.com..")
        private String personalUrl;

        @ApiModelProperty(value = "이메일 수신 동의 여부", example = "true/false")
        private boolean infoByEmail;

        @ApiModelProperty(value = "웹 수신 동의 여부", example = "true/false")
        private boolean infoByWeb;
    }

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

        @ApiModelProperty(value = "이메일 인증 여부", example = "true/false")
        private boolean emailVerified;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .email(user.getEmail())
                    .introduction(user.getIntroduction())
                    .personalUrl(user.getPersonalUrl())
                    .infoByEmail(user.getInfoAgree().isInfoByEmail())
                    .infoByWeb(user.getInfoAgree().isInfoByWeb())
                    .emailVerified(user.isEmailVerified())
                    .build();
        }
    }
}
