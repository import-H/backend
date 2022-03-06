package com.importH.domain.user.repository;

import com.importH.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> , UserCustomRepository {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"refreshToken"})
    Optional<User> findWithTokenByEmail(String email);

    @EntityGraph(attributePaths = {"refreshToken"})
    Optional<User> findWithTokenById(Long id);

    Optional<User> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPathId(String pathId);

    Long countByEmailVerified(boolean verified);

    Optional<User> findByOauthId(String oauthId);

}
