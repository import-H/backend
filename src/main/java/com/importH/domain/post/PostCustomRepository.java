package com.importH.domain.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostCustomRepository {

    Slice<Post> findAllPostsByType(String type , Pageable pageable);

}
