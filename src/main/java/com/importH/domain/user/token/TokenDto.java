package com.importH.domain.user.token;

import com.importH.domain.user.entity.User;
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

