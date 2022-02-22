package com.importH.core.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> , UserCustomRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmailAndProvider(String email, String provider);


    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPathId(String pathId);

    Long countByEmailVerified(boolean verified);
}
