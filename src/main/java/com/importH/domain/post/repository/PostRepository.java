package com.importH.domain.post.repository;

import com.importH.domain.post.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post,Long>, PostCustomRepository {

    boolean existsByTitle(String title);

    Post findByTitle(String title);

    int countByType(String type);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findById(Long aLong);

//    @Query("select p  from Post p left join fetch p.scraps scrap where p.id=:postId")
    @EntityGraph(attributePaths = {"user","scraps"})
    Optional<Post> findPostWithScrapById(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"user","likes"})
    Optional<Post> findPostWithLikeById(Long postId);
}
