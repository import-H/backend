package com.importH.domain.post.repository;

import com.importH.domain.post.dto.ScrapDto;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostScrap;
import com.importH.domain.post.entity.PostType;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostScrapRepositoryTest {

    @Autowired
    PostScrapRepository postScrapRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 스크랩 가져오기 페이징 확인")
    void findAllScraps() throws Exception {
        // given

        User user = userRepository.save(User.builder().nickname("테스트").email("테스트").build());
        for (int i = 0; i < 100; i++) {
            Post post = postRepository.save(Post.builder().user(user).title("테스트" +i).content("테스트"+ i).type(PostType.FREE.getType()).important(false).build());
            postScrapRepository.save(PostScrap.create(post, user));
        }

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScrapDto.Response> allByUser = postScrapRepository.findAllByUser(user, pageRequest);

        //then
        assertThat(allByUser.getSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("유저 스크랩 가져오기 본인 게시글만 가져오는지 확인")
    void findAllScraps_02() throws Exception {
        // given
        User user = userRepository.save(User.builder().nickname("테스트1").email("테스트1").build());
        for (int i = 0; i < 5; i++) {
            Post post = postRepository.save(Post.builder().user(user).title("테스트" +i).content("테스트"+ i).type(PostType.FREE.getType()).important(false).build());
            postScrapRepository.save(PostScrap.create(post, user));
        }

        User another = userRepository.save(User.builder().nickname("테스트2").email("테스트2").build());
        for (int i = 0; i < 5; i++) {
            Post post = postRepository.save(Post.builder().user(another).title("테스트" +i).content("테스트"+ i).type(PostType.FREE.getType()).important(false).build());
            postScrapRepository.save(PostScrap.create(post, another));
        }

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ScrapDto.Response> allByUser = postScrapRepository.findAllByUser(user, pageRequest);

        //then
        assertThat(allByUser).hasSize(5);
    }

}