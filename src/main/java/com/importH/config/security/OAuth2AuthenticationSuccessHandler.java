package com.importH.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.jwt.TokenDto;
import com.importH.core.service.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final SignService signService;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2User = (OAuth2AuthenticationToken) authentication;

        User user = saveOrUpdate(oAuth2User.getPrincipal().getAttributes(), oAuth2User.getAuthorizedClientRegistrationId());

        TokenDto token = signService.socialLogin(user);
        log.info("accessToken : {} , refreshToken : {}", token.getAccessToken(), token.getRefreshToken());
        writeTokenResponse(response, token);
    }

    // 유저 생성 및 수정 서비스 로직
    private User saveOrUpdate(Map<String, Object> attributes, String provider){
        User user = userRepository.findByEmailAndProvider((String) attributes.get("email"), provider)
                .orElseGet(() -> toEntity(attributes,provider));
        return userRepository.save(user);
    }
    private void writeTokenResponse(HttpServletResponse response, TokenDto token) throws IOException {
//        response.setContentType("text/html;charset=UTF-8");
//
        response.addHeader("Auth", token.getAccessToken());
        response.addHeader("Refresh", token.getRefreshToken());
    }

    public User toEntity(Map<String, Object> attributes, String provider){
        return User.builder()
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profileImage((String) attributes.get("picture"))
                .provider(provider)
                .role("ROLE_USER") // 기본 권한 GUEST
                .build();
    }

}