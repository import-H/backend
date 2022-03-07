package com.importH.domain.post.repository;

import com.importH.domain.post.entity.Post;
import com.importH.domain.post.repository.PostCustomRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post,Long>, PostCustomRepository {

    boolean existsByTitle(String title);

    Post findByTitle(String title);

    int countByType(String type);

    @EntityGraph(attributePaths = {"user","tags","comments"})
    Optional<Post> findWithTagsAndCommentsById(Long  postId);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findById(Long aLong);
}
