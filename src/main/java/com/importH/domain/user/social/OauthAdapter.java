package com.importH.domain.user.social;

import com.importH.global.config.OauthProperties;

import java.util.HashMap;
import java.util.Map;

public interface OauthAdapter {

    static Map<String, OauthProvider> getOauthProviders(OauthProperties properties) {
        Map<String, OauthProvider> oauthProvider = new HashMap<>();

        properties.getUser().forEach((key, value) -> oauthProvider.put(key,
                new OauthProvider(value, properties.getProvider().get(key))));
        return oauthProvider;
    }

    OauthTokenResponse getToken(String code, OauthProvider provider);

    SocialProfile getUserProfile(String providerName, OauthTokenResponse tokenResponse, OauthProvider provider);

}