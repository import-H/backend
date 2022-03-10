package com.importH.domain.post.repository;

import com.importH.domain.post.entity.Post;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PostCustomRepository {

    Slice<Post> findAllPostsByType(String type , Pageable pageable);

    List<Post> findAllByImportantIsTrue();

    Optional<Post> findWithAllById(Long id);


    Page<UserPostDto.Response> findAllPostByUser(User user, Pageable pageable);

}
