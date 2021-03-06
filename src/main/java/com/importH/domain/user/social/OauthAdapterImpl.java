package com.importH.domain.user.social;

import com.importH.global.error.code.SocialErrorCode;
import com.importH.global.error.exception.SocialException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class OauthAdapterImpl implements OauthAdapter {

    @Override
    public OauthTokenResponse getToken(String code, OauthProvider provider) {
        return WebClient.create()
                .post()
                .uri(provider.getTokenUrl())
                .headers(header -> {
                    header.setBasicAuth(provider.getClientId(), provider.getClientSecret());
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code, provider))
                .retrieve()
                .bodyToMono(OauthTokenResponse.class)
                .block();
    }
    private MultiValueMap<String, String> tokenRequest(String code, OauthProvider provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUrl());
        return formData;
    }


    private Map<String, Object> getUserAttributes(OauthProvider provider, OauthTokenResponse tokenResponse) {

        Map<String, Object> userAttributes = new LinkedHashMap<>();
        try {
            userAttributes = WebClient.create()
                    .get()
                    .uri(provider.getUserInfoUrl())
                    .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[SocialLogin Error] : {}" , e.getResponseBodyAsString());
            throw new SocialException(SocialErrorCode.NOT_VALID_ACCESS);
        }
        return userAttributes;
    }
    @Override
    public SocialProfile getUserProfile(String providerName, OauthTokenResponse tokenResponse, OauthProvider provider) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);

        return OauthAttributes.extract(providerName, userAttributes);
    }
}
