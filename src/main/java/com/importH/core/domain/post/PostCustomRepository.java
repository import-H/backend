package com.importH.core.domain.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostCustomRepository {

    Slice<Post> findPostsAllOrderByLike(Pageable pageable);
}
