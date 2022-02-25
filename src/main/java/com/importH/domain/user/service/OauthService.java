package com.importH.domain.user.service;

import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.social.*;
import com.importH.domain.user.token.TokenDto;
import com.importH.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {

    private final OauthProviderRepository oauthProviderRepository;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final OauthAdapterImpl oauthAdapter;

    @Transactional
    public TokenDto socialLogin(String providerName, String code) {
        OauthProvider provider = oauthProviderRepository.findByProviderName(providerName);

        OauthTokenResponse tokenResponse = oauthAdapter.getToken(code, provider);

        SocialProfile socialProfile = oauthAdapter.getUserProfile(providerName, tokenResponse, provider);

        User user = saveOrUpdate(socialProfile);

        TokenDto token = jwtProvider.createToken(TokenDto.Info.fromEntity(user), user.getRole());

        return token;
    }

    private User saveOrUpdate(SocialProfile socialProfile) {
        User member = userRepository.findByOauthId(socialProfile.getOauthId())
                .map(entity -> entity.update(
                        socialProfile.getEmail(), socialProfile.getName(), socialProfile.getImageUrl()))
                .orElseGet(() -> userRepository.save(socialProfile.toUser()));
        return member;
    }


}
