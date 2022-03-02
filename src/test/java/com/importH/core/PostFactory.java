package com.importH.core;

import com.importH.domain.post.Post;
import com.importH.domain.post.PostDto;
import com.importH.domain.post.PostRepository;
import com.importH.domain.post.PostService;
import com.importH.domain.tag.TagDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
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
                    .user(userRepository.save(User.builder().nickname("test"+i).email("test@"+i).password("test"+i).role("ROLE_USER").pathId("t"+i).weekAgree(false).build()))
                    .likeCount(i)
                    .build();
            postRepository.save(build);
        }
    }

    public Post createPost(User user) {
        return createPost(user, getRequest("테스트","테스트"));
    }

    public Post createPost(User user, PostDto.Request request) {
       return postService.registerPost(user,request);
    }

    public PostDto.Request getRequest(String title, String content, String... tagName) {
        return PostDto.Request.
                builder()
                .title(title)
                .content(content)
                .type("free")
                .tags(Arrays.stream(tagName).map(name -> TagDto.builder().name(name).build()).collect(Collectors.toList()))
                .build();
    }
}
