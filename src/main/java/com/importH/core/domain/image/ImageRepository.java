package com.importH.core.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ImageRepository extends JpaRepository<Image, Long> {

}
