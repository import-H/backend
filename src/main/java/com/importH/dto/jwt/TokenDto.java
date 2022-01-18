package com.importH.dto.jwt;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;
}
