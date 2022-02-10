package com.importH.core;

import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.sign.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Transactional
@RequiredArgsConstructor
public class AccountFactory {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createNewAccount(String nickname) {

        return userRepository.save(User.builder()
                .email(nickname + "@email.com")
                .nickname(nickname)
                .introduction("테스트 입니다.")
                .personalUrl("http://.com")
                .password(nickname + "asd")
                .weekAgree(true)
                .build());
    }
}