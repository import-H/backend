package com.importH.global.event;

import com.importH.core.PostFactory;
import com.importH.core.UserFactory;
import com.importH.core.WithAccount;
import com.importH.domain.comment.CommentDto;
import com.importH.domain.comment.CommentService;
import com.importH.domain.notification.NotificationService;
import com.importH.domain.post.Post;
import com.importH.domain.post.PostRepository;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class GlobalEventListenerTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentService commentService;


    @Autowired
    UserRepository userRepository;

    @Autowired
    PostFactory postFactory;

    @Autowired
    UserFactory userFactory;

    @MockBean
    NotificationService notificationService;

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 댓글 등록시 해당 게시글 유저에게 알람 - 알람 설정 on 인 유저 알람 생성")
    void createNotification_success_on() throws Exception {
        // given
        User user = userFactory.createNewAccount("테스트02", true,true);
        Post post = postFactory.createPost(user);
        User loginUser = userRepository.findByNickname("테스트").get();
        given(notificationService.createNotification(any())).willReturn(100L);

        // when
       commentService.registerComment(post.getId(), loginUser, getRequest("테스트 댓글"));

        //then
        verify(notificationService, times(1)).createNotification(any());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 게시글 주인이 댓글 등록시 알람 생성 X")
    void createNotification_success_author() throws Exception {
        // given
        User loginUser = userRepository.findByNickname("테스트").get();
        Post post = postFactory.createPost(loginUser);
        given(notificationService.createNotification(any())).willReturn(100L);

        // when
        commentService.registerComment(post.getId(), loginUser, getRequest("테스트 댓글"));

        //then
        verify(notificationService, never()).createNotification(any());

    }

    @Test
    @WithAccount("테스트")
    @DisplayName("[성공] 댓글 등록시 해당 게시글 유저에게 알람 - 알람 설정 off 인 유저는 알람 생성 x")
    void createNotification_success_off() throws Exception {
        // given
        User user = userFactory.createNewAccount("테스트02", false,true);
        Post post = postFactory.createPost(user);
        User loginUser = userRepository.findByNickname("테스트").get();

        // when
        commentService.registerComment(post.getId(), loginUser, getRequest("테스트 댓글"));

        //then
        verify(notificationService, never()).createNotification(any());

    }

    private CommentDto.Request getRequest(String content) {
        return CommentDto.Request.builder()
                .content(content)
                .build();
    }

}