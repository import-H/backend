package com.importH.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image>findAllByStoreFileNameIn(List<String> filename);

}
