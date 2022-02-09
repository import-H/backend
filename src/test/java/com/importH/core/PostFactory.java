package com.importH.core;

import com.importH.core.domain.user.User;
import com.importH.core.domain.post.Post;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class PostFactory {

    private final PostService postService;

    public Post createPost(User user, int type, PostDto.Request request) {
       return postService.registerPost(user, type,request);
    }

    public PostDto.Request getRequest(String title, String content, String... tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .content(content)
                .tags(Arrays.stream(tagName).map(name -> TagDto.builder().name(name).build()).collect(Collectors.toList()))
                .build();
    }
}
