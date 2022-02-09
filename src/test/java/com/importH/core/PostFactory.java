package com.importH.core;

import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class PostFactory {

    private final PostService postService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @PostConstruct
    void testData() {
        for (int i = 0; i < 50; i++) {
            Post build = Post.builder()
                    .title("test" + i)
                    .content("test" + i)
                    .user(userRepository.save(User.builder().nickname("test"+i).email("test@"+i).password("test"+i).weekAgree(false).build()))
                    .likeCount(i)
                    .build();
            postRepository.save(build);
        }
    }

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
