package com.importH.domain.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostCustomRepositoryImplTest {


    @Autowired
    PostRepository postRepository;


    @Test
    @DisplayName("전체 공지사항 가져오기")
    void findAllPost_01() throws Exception {

        // given

        List<Post> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            list.add(postRepository.save(Post.builder().title("테스트").content("테스트").type(PostType.FREE.getType()).important(true).build()));
        }
        for (int i = 0; i < 10; i++) {
            postRepository.save(Post.builder().title("테스트").content("테스트").type(PostType.FREE.getType()).important(false).build());
        }


        // when
        List<Post> posts = postRepository.findAllByImportantIsTrue();

        //then
        assertThat(posts).hasSize(3);

    }



}