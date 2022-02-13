package com.importH.core.domain.banner;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface BannerRepository extends JpaRepository<Banner,Long> {

    Optional<Banner> findByTitle(String title);

    @Override
    @EntityGraph(attributePaths = {"tags"})
    List<Banner> findAll();
}
