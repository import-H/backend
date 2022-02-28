package com.importH.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ApiModel(value = "소셜 로그인 게시판ID 요청 DTO")
public class SocialDto {

    @ApiModelProperty(value = "개인 게시판 id : 개인게시판 주소로 사용할 id 를 15자 이내 알파벳+숫자 로 입력해주세요", required = true, example = "sukeun1997")
    @NotBlank(message = "게시판 주소 id는 필수 입력 값 입니다.")
    @Pattern(regexp = "^[a-zA-z0-9]*$", message = "알파벳,숫자 조합으로만 입력이 가능합니다.")
    @Length(min = 5, max = 15, message = "5글자 이상 15글자 이하로 입력 해 주세요")
    private String pathId;


}
