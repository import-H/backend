package com.importH.core.service;

import com.importH.config.security.UserAccount;
import com.importH.core.dto.sign.UserResponseDto;
import com.importH.core.domain.account.Account;
import com.importH.core.error.exception.UserException;
import com.importH.core.domain.account.AccountRepository;
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

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
        return new UserAccount(account);
    }


    public UserResponseDto findById(Long userId) {
        Account account = accountRepository.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
        return new UserResponseDto(account);
    }
}
