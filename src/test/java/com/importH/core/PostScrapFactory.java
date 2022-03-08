package com.importH.core;

import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostScrap;
import com.importH.domain.post.repository.PostScrapRepository;
import com.importH.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PostScrapFactory {

    private final PostScrapRepository postScrapRepository;


    public void createScrap(User user, Post post) {
        postScrapRepository.save(PostScrap.create(post, user));
    }
}
