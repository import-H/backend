package com.importH.global.config;

import com.importH.domain.user.social.OauthProviderRepository;
import com.importH.domain.user.social.OauthAdapter;
import com.importH.domain.user.social.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(OauthProperties.class)
@RequiredArgsConstructor
public class OauthConfig {
    private final OauthProperties properties;

    @Bean
    public OauthProviderRepository inMemoryProviderRepository() {
        Map<String, OauthProvider> providers = OauthAdapter.getOauthProviders(properties);
        return new OauthProviderRepository(providers);
    }
}