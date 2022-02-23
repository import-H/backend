package com.importH.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post,Long>,PostCustomRepository  {

    Optional<Post> findByIdAndType(Long postId, String type);
    boolean existsByTitle(String title);

    Post findByTitle(String title);

    int countByType(String type);
}
