package com.importH.core.dto.email;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class EmailDto {

        @NotBlank(message = "이메일은 필수값 입니다.")
        private String email;

        @NotBlank(message = "link는 필수값 입니다.")
        private String link;

        @NotBlank(message = "닉네임은 필수값 입니다.")
        private String nickname;

        @NotBlank(message = "linkName은 필수값 입니다.")
        private String linkName;

        @NotBlank(message = "메세지는 필수값 입니다.")
        private String message;

        @NotBlank(message = "제목은 필수값 입니다.")
        private String subject;
}
