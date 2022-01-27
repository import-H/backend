package com.importH.repository;

import com.importH.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken , String> {

    Optional<RefreshToken> findByKey(Long key);
}
