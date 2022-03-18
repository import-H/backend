package com.importH.domain.user.service;

import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.social.*;
import com.importH.domain.user.token.TokenDto;
import com.importH.global.error.exception.SocialException;
import com.importH.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.importH.global.error.code.SocialErrorCode.SOCIAL_LOGIN_FAILED;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {

    private final OauthProviderRepository oauthProviderRepository;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final OauthAdapter oauthAdapter;

    @Transactional
    public TokenDto socialLogin(String providerName, String code) {
        OauthProvider provider = oauthProviderRepository.findByProviderName(providerName);

        OauthTokenResponse tokenResponse = oauthAdapter.getToken(code, provider);

        SocialProfile socialProfile = oauthAdapter.getUserProfile(providerName, tokenResponse, provider);

        User user = saveOrUpdate(socialProfile);

        TokenDto token = jwtProvider.createToken(user);

        return token;
    }

    private User saveOrUpdate(SocialProfile socialProfile) {

        Optional<User> user = userRepository.findByEmail(socialProfile.getEmail());

        if (user.isPresent()) {
            return user.get();
        }

        User member = user.orElseGet(() -> {
            validateProfile(socialProfile);
            return userRepository.save(socialProfile.toUser());
        });
        return member;
    }

    private void validateProfile(SocialProfile socialProfile) {
        if (socialProfile.getEmail() == null || socialProfile.getName() == null) {
            throw new SocialException(SOCIAL_LOGIN_FAILED);
        }
    }


}
