package com.importH.core.dto.jwt;

import com.importH.core.domain.user.User;
import lombok.*;

import javax.sound.sampled.DataLine.Info;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class Info {
        private Long id;

        private String email;
        private String nickname;
        private String profileImage;
        private boolean emailVerified;

        public static Info fromEntity(User user) {
            return Info.builder().email(user.getEmail())
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .emailVerified(user.isEmailVerified())
                    .profileImage(user.getProfileImage()).build();
        }
    }

}

