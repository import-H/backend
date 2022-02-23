package com.importH.config.security.oauth;

import com.importH.core.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {
    private final String oauthId;
    private final String email;
    private final String name;
    private final String imageUrl;

    @Builder
    public UserProfile(String oauthId, String email, String name, String imageUrl) {
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public User toMember() {
        return User.builder()
                .oauthId(oauthId)
                .email(email)
                .nickname(name)
                .profileImage(imageUrl)
                .role("ROLE_USER")
                .weekAgree(true)
                .build();
    }
}