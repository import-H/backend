package com.importH.service;

import com.importH.domain.User;
import com.importH.dto.UserLoginResponseDto;
import com.importH.dto.UserSignUpRequestDto;
import com.importH.error.exception.UserException;
import com.importH.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.importH.error.code.UserErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
        return user;
    }

    public Long signup(UserSignUpRequestDto userSignUpRequestDto) {
        if (userRepository.findByEmail(userSignUpRequestDto.getEmail()).orElse(null) == null) {
            return userRepository.save(userSignUpRequestDto.toEntity()).getId();
        }
        throw new UserException(USER_EMAIL_DUPLICATED);
    }


    public UserLoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(EMAIL_LOGIN_FAILED));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(EMAIL_LOGIN_FAILED);
        }
        return new UserLoginResponseDto(user);
    }


}
