package com.importH.dto.sign;

import com.importH.domain.Account;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collections;

@Data
@ApiModel("회원 가입 요청 DTO")
public class UserSignUpRequestDto {

    @ApiModelProperty(value = "홍익 이메일" , example = "abc@hongik.ac.kr", required = true)
    @Email(message = "이메일 형태로 입력 해 주세요.")
    @NotBlank(message = "필수 입력 값 입니다.")
    private String email;

    @ApiModelProperty(value = "비밀번호" , required = true)
    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Length(min = 8, max = 50, message = "8자 이상 50자 이내로 입력 해주세요")
    private String password;

    @ApiModelProperty(value = "닉네임" , required = true)
    @NotBlank(message = "닉네임은 필수 입력 값 입니다.")
    @Pattern(regexp = "^[a-zA-zㄱ-ㅎ가-힣0-9]*$", message = "영어 또는 한글로만 입력이 가능합니다.")
    @Length(min = 3, max = 20, message = "3글자 이상 20글자 이하로 입력 해 주세요")
    private String nickname;


    public Account toEntity() {
        return Account.builder()
                .email(email)
                .password(password)
                .nickName(nickname)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }
}
