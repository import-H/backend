package com.importH.core.service;

import com.importH.core.PostFactory;
import com.importH.core.UserFactory;
import com.importH.core.WithAccount;
import com.importH.domain.post.dto.PostDto;
import com.importH.domain.post.dto.PostDto.Response;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostType;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.post.service.PostService;
import com.importH.domain.tag.Tag;
import com.importH.domain.tag.TagDto;
import com.importH.domain.tag.TagService;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.exception.PostException;
import com.importH.global.error.exception.SecurityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
class PostServiceTest {

    public static final String FREE = "free";
    public static final String QUESTIONS = "questions";
    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    UserFactory userFactory;

    @Autowired
    TagService tagService;

    User user;
    Post post;

    @BeforeEach
    void before() {
        user = userFactory.createNewAccount("test", "test" + "@email.com", "pathId", true);
        post = postService.registerPost(user , getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE));
    }

    @AfterEach
    void after() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("[??????] ????????? ?????? ???????????? ??????")
    void registerPost_success() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE);

        // when
        Post post = postService.registerPost(user, request);


        //then
        assertThat(post).hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("content", request.getContent());
        assertThat(post.getTags()).contains(tagService.findByTitle("??????"));
    }

    @Test
    @DisplayName("[??????] ????????? ?????? - ???????????? ?????? ?????????")
    void registerPost_fail() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????", "????????? ????????? ?????????.", "??????", "FREES");

        // when
        PostException exception = assertThrows(PostException.class, () -> postService.registerPost(user, request));


        //then
        assertThat(exception).hasMessageContaining(exception.getErrorMessage());
    }


    @Test
    @DisplayName("[??????] ????????? ?????? ???????????? ??????")
    void getPost_success() throws Exception {
        // given
        Post post =  postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE));

        // when
        Response response = postService.getPost(user, post.getId());

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("responseInfo.boardId", post.getType())
                .hasFieldOrPropertyWithValue("responseInfo.postId", post.getId())
                .hasFieldOrPropertyWithValue("responseInfo.title", post.getTitle())
                .hasFieldOrPropertyWithValue("responseInfo.content", post.getContent())
                .hasFieldOrPropertyWithValue("responseInfo.nickname", post.getUser().getNickname())
                .hasFieldOrPropertyWithValue("responseInfo.profileImage", post.getUser().getProfileImage())
                .hasFieldOrPropertyWithValue("responseInfo.likeCount", post.getLikeCount())
                .hasFieldOrPropertyWithValue("responseInfo.important", post.isImportant())
                .hasFieldOrPropertyWithValue("responseInfo.viewCount", post.getViewCount());

        assertThat(response.getResponseInfo().getTags()).hasSameElementsAs(tagService.getTagDtos(post.getTags()));
        assertThat(response.getComments()).hasSameElementsAs(postService.getCommentDtos(post));
    }

    @Test
    @DisplayName("[??????] ????????? ?????? ???????????? ?????? - ?????? ???????????? ?????????")
    void getPost_success_important() throws Exception {
        // given
        Post post =  postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE,true));

        // when
        Response response = postService.getPost(user, post.getId());

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("responseInfo.boardId", post.getType())
                .hasFieldOrPropertyWithValue("responseInfo.postId", post.getId())
                .hasFieldOrPropertyWithValue("responseInfo.title", post.getTitle())
                .hasFieldOrPropertyWithValue("responseInfo.content", post.getContent())
                .hasFieldOrPropertyWithValue("responseInfo.nickname", post.getUser().getNickname())
                .hasFieldOrPropertyWithValue("responseInfo.profileImage", post.getUser().getProfileImage())
                .hasFieldOrPropertyWithValue("responseInfo.likeCount", post.getLikeCount())
                .hasFieldOrPropertyWithValue("responseInfo.important", post.isImportant())
                .hasFieldOrPropertyWithValue("responseInfo.viewCount", post.getViewCount());

        assertThat(post.isImportant()).isTrue();
        assertThat(response.getResponseInfo().getTags()).hasSameElementsAs(tagService.getTagDtos(post.getTags()));
        assertThat(response.getComments()).hasSameElementsAs(postService.getCommentDtos(post));
    }

    @Test
    @DisplayName("[??????] ????????? ?????? - ???????????? ?????? ????????? ??????")
    void getPost_fail() throws Exception {
        // given
        Long postId = 99999999L;

        // when
        PostErrorCode notFoundPost = PostErrorCode.NOT_FOUND_POST;

        PostException postException = assertThrows(PostException.class, () -> postService.getPost(user, postId));

        //then
        assertThat(postException).hasMessageContaining(notFoundPost.getDescription());

    }


    @Test
    @DisplayName("[??????] ????????? ??????")
    void updatePost_success() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????2", "????????? ????????? ?????????.3", "??????1", FREE);

        // when
        postService.updatePost(user, post.getId(), request);

        //then
        assertThat(post)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("content", request.getContent());

        Tag tagBefore = tagService.findByTitle("??????");
        Tag tagAfter = tagService.findByTitle("??????1");
        assertThat(post.getTags()).contains(tagAfter);
        assertThat(post.getTags()).doesNotContain(tagBefore);
    }


    @Test
    @DisplayName("[??????] ????????? ?????? - ?????? ?????????????????? ?????????")
    void updatePost_success_setImportant() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????2", "????????? ????????? ?????????.3", "??????1", PostType.NOTICE.getType(), true);

        // when
        postService.updatePost(user, post.getId(), request);

        //then
        assertThat(post)
                .hasFieldOrPropertyWithValue("title", request.getTitle())
                .hasFieldOrPropertyWithValue("important", request.isImportant())
                .hasFieldOrPropertyWithValue("content", request.getContent());

        Tag tagBefore = tagService.findByTitle("??????");
        Tag tagAfter = tagService.findByTitle("??????1");
        assertThat(post.getTags()).contains(tagAfter);
        assertThat(post.getTags()).doesNotContain(tagBefore);
    }




    @Test
    @DisplayName("[??????] ????????? ?????? - ??? ????????? ????????????")
    void updatePost_fail() throws Exception {
        // given
        PostDto.Request request = getRequest("?????????2", "????????? ????????? ?????????.3", "??????1", FREE);
        User test2 = User.builder().nickname("test2").build();
        // when
        SecurityException postException = assertThrows(SecurityException.class, () -> postService.updatePost(test2, post.getId(), request));

        //then
        assertThat(postException).hasMessageContaining(SecurityErrorCode.ACCESS_DENIED.getDescription());
    }

    @Test
    @DisplayName("[??????] ????????? ??????")
    void deletePost_success() throws Exception {
        // given

        // when
        postService.deletePost(user, post.getId());

        //then
        assertThat(postRepository.existsById(post.getId())).isFalse();

    }

    @Test
    @DisplayName("[??????] ????????? ?????? - ??? ???????????? ????????????")
    void deletePost_fail() throws Exception {
        // given
        User test2 = User.builder().nickname("test2").build();
        // when
        SecurityException postException = assertThrows(SecurityException.class, () -> postService.deletePost(test2, post.getId()));

        //then
        assertThat(postException).hasMessageContaining(SecurityErrorCode.ACCESS_DENIED.getDescription());

    }

    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ????????? ????????? ?????? ?????? ??????????????? ?????? ")
    void findAll_success_01() throws Exception {
        // given
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", QUESTIONS));
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(FREE,pageRequest);

        //then
        assertThat(allPost.size()).isEqualTo(postRepository.countByType(FREE));
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.important")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ?????? ???????????? ?????? ?????? ??????????????? ?????? ")
    void findAll_success_02() throws Exception {
        // given
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE));
        }
        for (int i = 0; i < 5; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", QUESTIONS));
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(null,pageRequest);

        //then
        assertThat(allPost.size()).isEqualTo(10);
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.important")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ???????????? ???????????? ????????????")
    void findAll_success_03() throws Exception {
        // given
        for (int i = 0; i < 3; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE,true));
        }
        for (int i = 0; i < 10; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", QUESTIONS));
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(QUESTIONS,pageRequest);

        //then
        assertThat(allPost.size()).isEqualTo(13);
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.important")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ????????? ?????? - ???????????? ???????????? ????????????")
    void findAll_success_04() throws Exception {
        // given
        for (int i = 0; i < 3; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", FREE,true));
        }
        for (int i = 0; i < 10; i++) {
            postService.registerPost(user, getRequest("?????????", "????????? ????????? ?????????.", "??????", user.getPathId()));
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        // when
        List<PostDto.ResponseAll> allPost = postService.findAllPost(user.getPathId(),pageRequest);

        //then
        assertThat(allPost.size()).isEqualTo(10);
        allPost.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.important")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ?????? - ????????? ???????????? ?????? ??????")
    void findAllOrderByLike_success() throws Exception {

        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").descending());

        // when
        List<PostDto.ResponseAll> allList = postService.findAllPost(null, pageRequest);

        int maxLike = allList.stream().mapToInt(value -> value.getResponseInfo().getLikeCount()).max().getAsInt();

        //then
        assertThat(allList.size()).isEqualTo(10);
        assertThat(allList.get(0).getResponseInfo().getLikeCount()).isEqualTo(maxLike);
        allList.stream().forEach(responseAll -> assertThat(responseAll)
                .hasFieldOrProperty("responseInfo.boardId")
                .hasFieldOrProperty("responseInfo.postId")
                .hasFieldOrProperty("responseInfo.title")
                .hasFieldOrProperty("responseInfo.content")
                .hasFieldOrProperty("responseInfo.important")
                .hasFieldOrProperty("responseInfo.nickname")
                .hasFieldOrProperty("responseInfo.profileImage")
                .hasFieldOrProperty("responseInfo.likeCount")
                .hasFieldOrProperty("responseInfo.viewCount")
                .hasFieldOrProperty("commentsCount")
                .hasFieldOrProperty("thumbnail"));

    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ??????")
    void getPostByDeletedUser() throws Exception {
        // given
        user.delete();

        // when
        Response post = postService.getPost(user, this.post.getId());

        //then
        assertThat(post.getResponseInfo())
                .hasFieldOrPropertyWithValue("nickname","????????? ??????")
                .hasFieldOrPropertyWithValue("profileImage","N");
    }

    @Autowired
    PostFactory postFactory;

    @Test
    @DisplayName("????????? ????????? ?????? ??? ????????? ???????????? ")
    void findAllPostByLike_success() throws Exception {
        // given
        for (int i = 0; i < 10; i++) {
            Post post = postFactory.createPost(user);
        }

        PageRequest of = PageRequest.of(0, 10);
        // when

        List<UserPostDto.Response> allPostByWrote = postService.findAllPostByWrote(user, of);

        //then
        assertThat(allPostByWrote).hasSize(10);
    }

    @Autowired
    UserRepository userRepository;


    @Test
    @WithAccount("?????????")
    @DisplayName("????????? ????????? ?????? ??? ????????? 5??? ???????????? ")
    void findAllPost5ByLike_success() throws Exception {

        // given
        int size = 5;
        for (int i = 0; i < 6; i++) {
            Post post = postFactory.createPost(user);
        }

        User another = userRepository.findByNickname("?????????").get();
        for (int i = 0; i < size; i++) {
            Post post = postFactory.createPost(another);
        }

        PageRequest of = PageRequest.of(0, 10);
        // when

        List<UserPostDto.Response> allPostByWrote = postService.findAllPostByWrote(another, of);

        //then
        assertThat(allPostByWrote).hasSize(size);
    }




    private PostDto.Request getRequest(String title, String content, String tagName, String type) {
        return getRequest(title, content, tagName, type, false);
    }

    private PostDto.Request getRequest(String title, String content, String tagName, String type, boolean important) {
        return PostDto.Request.
                builder()
                .title(title)
                .type(type)
                .content(content)
                .important(important)
                .tags(List.of(TagDto.builder()
                        .name(tagName)
                        .build()))
                .build();
    }
}