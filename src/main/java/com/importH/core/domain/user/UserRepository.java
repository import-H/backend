package com.importH.core.domain.user;

import com.google.common.io.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> , UserCustomRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPathId(String pathId);

    Long countByEmailVerified(boolean verified);

    Optional<User> findByOauthId(String oauthId);
}
