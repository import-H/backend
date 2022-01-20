package com.importH.service.user;

import com.importH.config.security.UserAccount;
import com.importH.domain.Account;
import com.importH.dto.user.UserResponseDto;
import com.importH.error.exception.UserException;
import com.importH.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.importH.error.code.UserErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // TODO
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = userRepository.findByEmail(email).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
        return new UserAccount(account);
    }


    public UserResponseDto findById(Long userId) {
        Account account = userRepository.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
        return new UserResponseDto(account);
    }
}
