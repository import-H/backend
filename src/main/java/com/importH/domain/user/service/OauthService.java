package com.importH.domain.user.service;

import com.importH.domain.user.token.TokenDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.social.SocialProfile;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.social.OauthProviderRepository;
import com.importH.domain.user.social.OauthAttributes;
import com.importH.domain.user.social.OauthProvider;
import com.importH.domain.user.social.OauthTokenResponse;
import com.importH.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {

    private final OauthProviderRepository oauthProviderRepository;

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;


    @Transactional
    public TokenDto socialLogin(String providerName, String code) {
        OauthProvider provider = oauthProviderRepository.findByProviderName(providerName);

        OauthTokenResponse tokenResponse = getToken(code, provider);

        SocialProfile socialProfile = getUserProfile(providerName, tokenResponse, provider);

        User user = saveOrUpdate(socialProfile);

        TokenDto token = jwtProvider.createToken(TokenDto.Info.fromEntity(user), user.getRole());

        return token;
    }
    private OauthTokenResponse getToken(String code, OauthProvider provider) {
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

    private User saveOrUpdate(SocialProfile socialProfile) {
        User member = userRepository.findByOauthId(socialProfile.getOauthId())
                .map(entity -> entity.update(
                        socialProfile.getEmail(), socialProfile.getName(), socialProfile.getImageUrl()))
                .orElseGet(() -> userRepository.save(socialProfile.toUser()));
        return member;
    }

    private MultiValueMap<String, String> tokenRequest(String code, OauthProvider provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUrl());
        return formData;
    }

    private SocialProfile getUserProfile(String providerName, OauthTokenResponse tokenResponse, OauthProvider provider) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);

        return OauthAttributes.extract(providerName, userAttributes);
    }

    // OAuth 서버에서 유저 정보 map으로 가져오기
    private Map<String, Object> getUserAttributes(OauthProvider provider, OauthTokenResponse tokenResponse) {
        return WebClient.create()
                .get()
                .uri(provider.getUserInfoUrl())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
