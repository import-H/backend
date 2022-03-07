package com.importH.domain.post.repository;

import com.importH.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostCustomRepository {

    Slice<Post> findAllPostsByType(String type , Pageable pageable);

    List<Post> findAllByImportantIsTrue();

}
