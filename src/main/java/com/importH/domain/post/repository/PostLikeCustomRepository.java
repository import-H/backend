package com.importH.domain.post.repository;

import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLikeCustomRepository {

    Page<UserPostDto.Response> findAllByUser(User user, Pageable pageable);
}
