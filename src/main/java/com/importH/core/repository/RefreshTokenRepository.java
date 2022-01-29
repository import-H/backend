package com.importH.core.repository;

import com.importH.core.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional(readOnly = true)
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , String> {

    Optional<RefreshToken> findByKey(Long key);

    boolean existsByToken(String refreshToken);
}
