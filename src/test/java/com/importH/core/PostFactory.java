package com.importH.core;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.dto.post.PostDto;
import com.importH.core.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PostFactory {

    private final PostService postService;

    public Post createPost( Account account, int type,PostDto.Request request) {
       return postService.registerPost(account, type,request);
    }
}
