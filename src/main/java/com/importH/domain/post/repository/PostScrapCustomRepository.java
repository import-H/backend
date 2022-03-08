package com.importH.domain.post.repository;

import com.importH.domain.post.dto.ScrapDto;
import com.importH.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostScrapCustomRepository {

    Page<ScrapDto.Response> findAllByUser(User user, Pageable pageable);
}
