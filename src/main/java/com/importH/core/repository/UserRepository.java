package com.importH.core.repository;

import com.importH.core.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}
