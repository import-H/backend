package com.importH.core.service.user;

import com.importH.config.security.UserAccount;
import com.importH.core.dto.user.UserResponseDto;
import com.importH.core.entity.Account;
import com.importH.core.error.exception.UserException;
import com.importH.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.importH.core.error.code.UserErrorCode.NOT_FOUND_USERID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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
