package com.importH.domain.user.social;

import java.util.HashMap;
import java.util.Map;

public class OauthProviderRepository {
    private final Map<String, OauthProvider> providers;

    public OauthProviderRepository(Map<String, OauthProvider> providers) {
        this.providers = new HashMap<>(providers);
    }

    public OauthProvider findByProviderName(String name) {
        return providers.get(name);
    }
}
