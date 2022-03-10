package com.importH.domain.user.token;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private boolean isNew = false;

    }





