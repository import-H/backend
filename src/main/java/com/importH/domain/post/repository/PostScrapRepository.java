package com.importH.domain.post.repository;

import com.importH.domain.post.entity.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PostScrapRepository extends JpaRepository<PostScrap,Long > {
}
