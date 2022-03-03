package com.importH.core;

import com.importH.domain.user.entity.InfoAgree;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.domain.user.token.RefreshToken;
import com.importH.domain.user.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class UserFactory {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;


    public User createNewAccount(String nickname, String email, String pathId, boolean emailVerified) {

        return createNewAccount(nickname,email,pathId,emailVerified,false,false,null);
    }

    public User createNewAccount(String nickname, boolean infoByWeb, boolean infoByEmail) {
        return createNewAccount(nickname, nickname, nickname, true, infoByWeb, infoByEmail,null);
    }

    public User createNewAccount(String nickname, String email, String pathId, boolean emailVerified, boolean infoByWeb , boolean infoByEmail, RefreshToken token) {

        User save = userRepository.save(User.builder()
                .email(email)
                .nickname(nickname)
                .introduction("테스트 입니다.")
                .personalUrl("http://.com")
                .password(passwordEncoder.encode("12341234"))
                .pathId(pathId)
                .emailVerified(emailVerified)
                .weekAgree(true)
                .infoAgree(new InfoAgree(infoByEmail, infoByWeb))
                .build());
        save.setToken(token);
        return save;
    }
}