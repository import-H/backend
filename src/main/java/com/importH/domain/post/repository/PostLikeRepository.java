package com.importH.domain.post.repository;

import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostLike;
import com.importH.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    boolean existsByUserAndPost(User user, Post post);

    boolean existsByPostId(Long id);

    Optional<PostLike> findByPostAndUser(Post post, User user);
}
