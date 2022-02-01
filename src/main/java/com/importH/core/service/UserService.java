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
        Account account = findByEmail(email);
        return new UserAccount(account);
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
    }


    /**
     * 유저 정보 조회
     */
    public UserResponseDto findUserById(Long userId) {
        return new UserResponseDto(findById(userId));
    }

    public Account findById(Long userId) {
        return accountRepository.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
    }

}
