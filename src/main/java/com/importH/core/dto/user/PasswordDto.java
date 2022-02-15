package com.importH.core.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "패스워드 변경 DTO")
public class PasswordDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ApiModel(value = "패스워드 변경 요청 DTO")
    public static class Request {

        @ApiModelProperty(value = "비밀번호 : 8자 이상 50자 이내로 입력 해주세요" , required = true ,example = "12341234")
        @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
        @Length(min = 8, max = 50, message = "8자 이상 50자 이내로 입력 해주세요")
        private String password;

        @ApiModelProperty(value = "위와 동일한 비밀번호를 입력 해주세요" , required = true ,example = "12341234")
        @NotBlank(message = "비밀번호 확인은 필수 입력 값 입니다.")
        @Length(min = 8, max = 50, message = "8자 이상 50자 이내로 입력 해주세요")
        private String confirmPassword;
    }
}
