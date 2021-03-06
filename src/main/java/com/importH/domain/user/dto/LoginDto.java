package com.importH.domain.user.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ApiModel(value = "로그인 요청 DTO")
public class LoginDto {

        @ApiModelProperty(value = "이메일", example = "abc@hongik.ac.kr")
        @NotBlank(message = "이메일은 필수 입력값 입니다.")
        private String email;

        @ApiModelProperty(value = "비밀번호", example = "12341234")
        @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
        private String password;

}
