package com.importH.core.dto.sign;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {

    private String email;
    private String password;
}
