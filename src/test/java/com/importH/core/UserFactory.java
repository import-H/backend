package com.importH.core;

import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
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

    public User createNewAccount(String nickname, String email, String pathId, boolean emailVerified) {

        return userRepository.save(User.builder()
                .email(email)
                .nickname(nickname)
                .introduction("테스트 입니다.")
                .personalUrl("http://.com")
                .password(nickname + "asd")
                .pathId(pathId)
                .emailVerified(emailVerified)
                .weekAgree(true)
                .build());
    }
}