package com.importH.domain.user.social;

import com.importH.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialProfile {
    private final String oauthId;
    private final String email;
    private final String name;
    private final String imageUrl;


    public User toUser() {
        return User.builder()
                .oauthId(oauthId)
                .email(email)
                .nickname(name)
                .profileImage(imageUrl)
                .role("ROLE_USER")
                .weekAgree(true)
                .emailVerified(true)
                .build();
    }
}