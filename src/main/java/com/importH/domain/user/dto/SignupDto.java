package com.importH.domain.user.dto;

import com.importH.domain.user.entity.InfoAgree;
import com.importH.domain.user.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel("회원 가입 요청 DTO")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    @ApiModelProperty(value = "홍익 이메일" , example = "abc@hongik.ac.kr", required = true)
    @Email(message = "이메일 형태로 입력 해 주세요.")
    @NotBlank(message = "필수 입력 값 입니다.")
    private String email;

    @ApiModelProperty(value = "비밀번호 : 8자 이상 50자 이내로 입력 해주세요" , required = true ,example = "12341234")
    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Length(min = 8, max = 50, message = "8자 이상 50자 이내로 입력 해주세요")
    private String password;

    @ApiModelProperty(value = "위와 동일한 비밀번호를 입력 해주세요" , required = true ,example = "12341234")
    @NotBlank(message = "비밀번호 확인은 필수 입력 값 입니다.")
    @Length(min = 8, max = 50, message = "8자 이상 50자 이내로 입력 해주세요")
    private String confirmPassword;


    @ApiModelProperty(value = "닉네임 : 영어 또는 한글로만 3~20자로 입력해주세요", required = true, example = "닉네임")
    @NotBlank(message = "닉네임은 필수 입력 값 입니다.")
    @Pattern(regexp = "^[a-zA-zㄱ-ㅎ가-힣0-9]*$", message = "알파벳,한글,숫자 조합으로만 입력이 가능합니다.")
    @Length(min = 2, max = 20, message = "2글자 이상 20글자 이하로 입력 해 주세요")
    private String nickname;

    @ApiModelProperty(value = "개인 게시판 id : 개인게시판 주소로 사용할 id 를 15자 이내 알파벳+숫자 로 입력해주세요", required = true, example = "sukeun1997")
    @NotBlank(message = "게시판 주소 id는 필수 입력 값 입니다.")
    @Pattern(regexp = "^[a-zA-z0-9]*$", message = "알파벳,숫자 조합으로만 입력이 가능합니다.")
    @Length(min = 5, max = 15, message = "5글자 이상 15글자 이하로 입력 해 주세요")
    private String pathId;

    @ApiModelProperty(value = "주 1회 활동 동의", required = true, example = "true , false")
    @NotNull
    private boolean agree;


    public User toEntity() {
        return User.builder()
                .email(email)
                .pathId(pathId)
                .password(password)
                .nickname(nickname)
                .role("ROLE_USER")
                .weekAgree(agree)
                .infoAgree(new InfoAgree(true,true))
                .build();
    }

}
