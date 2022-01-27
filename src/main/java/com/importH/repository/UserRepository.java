package com.importH.repository;

import com.importH.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByEmail(String email);
}
