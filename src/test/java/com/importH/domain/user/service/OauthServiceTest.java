package com.importH.domain.user.service;

import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.social.*;
import com.importH.domain.user.token.TokenDto;
import com.importH.global.error.code.SocialErrorCode;
import com.importH.global.error.exception.SocialException;
import com.importH.global.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    OauthProviderRepository oauthProviderRepository;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    OauthAdapterImpl oauthAdapter;

    @InjectMocks
    OauthService oauthService;

    private final String CODE = "4%2F0AX4XfWgQ6WIy8ZylF8i9oSHrxaqTtL6UQDzZFzlsbsKUlO4Gv5rsGP90nqsSpMhf5Vi92g";
    private final String ACCESS_TOKEN = "ya29.A0ARrdaM8BYJX0A8lLqnkOGU_wEO6_7-UQvYVFDa6yvJd46wOKmU0dq_x7Zro3zrswnR45ISbNSos2C3_V24oVjJRgjAjFPAh0lfLzG5jXMIgKiTGgLG9DWcS4J6c72KDNt42hTyBAgK6GAUTTB3X-yuHAe9MYeg";

    @Test
    @DisplayName("[성공] 소셜 로그인 및 계정 저장")
    void googleLogin_Success() throws Exception {
        // given
        String provider = "google";

        given(oauthProviderRepository.findByProviderName(any())).willReturn(getOauthProvider());
        given(oauthAdapter.getToken(any(), any())).willReturn(OauthTokenResponse.builder().accessToken(ACCESS_TOKEN).build());
        given(oauthAdapter.getUserProfile(any(),any(),any())).willReturn(getSocialProfile("test@mail.com"));
        given(jwtProvider.createToken(any(), any())).willReturn(getTokenDto());
        given(userRepository.save(any())).willReturn(getUser());

        // when
        TokenDto tokenDto = oauthService.socialLogin(provider, CODE);


        //then
        assertThat(tokenDto)
                .hasFieldOrProperty("accessToken")
                .hasFieldOrProperty("refreshToken");

        verify(oauthAdapter, times(1)).getToken(any(), any());
        verify(oauthAdapter, times(1)).getUserProfile(any(), any(), any());
        verify(userRepository, times(1)).save(any());
        verify(jwtProvider, times(1)).createToken(any(), any());
        verify(userRepository, times(1)).findByOauthId(any());
    }

    @Test
    @DisplayName("[성공] 소셜 로그인 및 계정 저장 - 이미 가입된 계정시 save 하지 않음")
    void googleLogin_Success_Not_Save() throws Exception {
        // given
        String provider = "google";

        given(oauthProviderRepository.findByProviderName(any())).willReturn(getOauthProvider());
        given(oauthAdapter.getToken(any(), any())).willReturn(OauthTokenResponse.builder().accessToken(ACCESS_TOKEN).build());
        given(oauthAdapter.getUserProfile(any(),any(),any())).willReturn(getSocialProfile("test@mail.com"));
        given(jwtProvider.createToken(any(), any())).willReturn(getTokenDto());
        given(userRepository.findByOauthId(any())).willReturn(Optional.ofNullable(getUser()));

        // when
        TokenDto tokenDto = oauthService.socialLogin(provider, CODE);


        //then
        assertThat(tokenDto)
                .hasFieldOrProperty("accessToken")
                .hasFieldOrProperty("refreshToken");

        verify(oauthAdapter, times(1)).getToken(any(), any());
        verify(oauthAdapter, times(1)).getUserProfile(any(), any(), any());
        verify(userRepository, times(1)).findByOauthId(any());
        verify(jwtProvider, times(1)).createToken(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("[실패] 소셜 로그인 및 계정 저장 - 가져온 유저정보에 이메일 or 이름 존재하지 않음")
    void googleLogin_fail() throws Exception {
        // given
        String provider = "google";
        SocialErrorCode errorCode = SocialErrorCode.SOCIAL_LOGIN_FAILED;

        given(oauthProviderRepository.findByProviderName(any())).willReturn(getOauthProvider());
        given(oauthAdapter.getToken(any(), any())).willReturn(OauthTokenResponse.builder().accessToken(ACCESS_TOKEN).build());
        given(oauthAdapter.getUserProfile(any(),any(),any())).willReturn(getSocialProfile(null));

        // when
        SocialException exception = assertThrows(SocialException.class, () -> oauthService.socialLogin(provider, CODE));


        //then
        assertThat(exception)
                .hasMessageContaining(errorCode.getDescription());

        verify(oauthAdapter, times(1)).getToken(any(), any());
        verify(oauthAdapter, times(1)).getUserProfile(any(), any(), any());
        verify(userRepository, times(1)).findByOauthId(any());
        verify(jwtProvider, never()).createToken(any(), any());
        verify(userRepository, times(1)).findByOauthId(any());
        verify(userRepository, never()).save(any());
    }

    private OauthProvider getOauthProvider() {
        return OauthProvider.builder().build();
    }

    private User getUser() {
        return User.builder().email(getSocialProfile("test@mail.com").getEmail()).role("ROLE_USER").build();
    }

    private SocialProfile getSocialProfile(String email) {
        return SocialProfile.builder().email(email).name("test").oauthId("1").build();
    }

    private TokenDto getTokenDto() {
        return TokenDto.builder().accessToken("accessToken").refreshToken("refreshToken").build();
    }
}