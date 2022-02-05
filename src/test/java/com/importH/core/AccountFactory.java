package com.importH.core;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.account.AccountRepository;
import com.importH.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.swagger2.mappers.ModelMapper;

@Component
@Transactional
@RequiredArgsConstructor
public class AccountFactory {

    private final AccountRepository accountRepository;

    public Account createNewAccount(String nickname) {

        return accountRepository.save(Account.builder()
                .email(nickname + "@email.com")
                .nickname(nickname)
                .password(nickname + "asd")
                .weekAgree(true)
                .build());
    }
}