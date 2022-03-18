package com.importH.domain.user.social;

import java.util.Arrays;
import java.util.Map;

public enum OauthAttributes {

    GITHUB("github") {
        @Override
        public SocialProfile of(Map<String, Object> attributes) {
            return SocialProfile.builder()
                    .oauthId(String.valueOf(attributes.get("id")))
                    .email((String) attributes.get("email"))
                    .login((String) attributes.get("login"))
                    .name((String) attributes.get("name"))
                    .imageUrl((String) attributes.get("avatar_url"))
                    .build();
        }
    },
    GOOGLE("google") {
        @Override
        public SocialProfile of(Map<String, Object> attributes) {
            return SocialProfile.builder()
                    .oauthId(String.valueOf(attributes.get("sub")))
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .imageUrl((String) attributes.get("picture"))
                    .build();
        }
    };

    private final String providerName;

    OauthAttributes(String name) {
        this.providerName = name;
    }

    public static SocialProfile extract(String providerName, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> providerName.equals(provider.providerName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)//TODO PROVIDER 없음 예외
                .of(attributes);
    }

    public abstract SocialProfile of(Map<String, Object> attributes);
}