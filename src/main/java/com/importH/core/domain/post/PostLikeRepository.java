package com.importH.core.domain.post;

import com.importH.core.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    boolean existsByAccountAndPost(Account account, Post post);

    boolean existsByPostId(Long id);

    Optional<PostLike> findByPostAndAccount(Post post, Account account);
}
