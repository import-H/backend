package com.importH.core.service;

import com.importH.core.domain.user.User;
import com.importH.core.domain.user.UserRepository;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostLikeRepository;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.post.CommentDto;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.error.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.importH.core.error.code.PostErrorCode.NOT_FOUND_POST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 저장
     */
    @Transactional
    public Post registerPost(User user, int boardType, PostDto.Request postRequestDto) {

        Post post = postRequestDto.toEntity();

        setPostRelation(user, boardType, postRequestDto, post);

        return savePost(post);
    }

    private void setPostRelation(User user, int boardType, PostDto.Request postRequestDto, Post post) {
        post.setTags(getTags(postRequestDto));
        post.setUser(user);
        post.setBoardType(boardType);
    }

    private Set<Tag> getTags(PostDto.Request postRequestDto) {
        return postRequestDto.getTags().stream()
                .map(TagDto::toEntity)
                .map(tag -> tagService.getTag(tag))
                .collect(Collectors.toSet());
    }

    private Post savePost(Post post) {
        Post save = postRepository.save(post);

        return save;
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostDto.Response getPost(User user, int boardId, Long postId) {

        Post post = findByTypeAndId(boardId, postId);

        increaseViewCount(user, post);

        Set<TagDto> tags = getTagDtos(post);
        List<CommentDto.Response> comments = getCommentDtos(post);

        boolean isLike = havePostLike(user, post);
        //TODO 계정 삭제시에 조회 어떻게 처리할것인지 생각

        return PostDto.Response.fromEntity(post, tags, comments, isLike);
    }

    private boolean havePostLike(User user, Post post) {
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    private void increaseViewCount(User user, Post post) {
        if (isNotAuthor(user, post)) {
            post.increaseView();
        }
    }

    private boolean isNotAuthor(User user, Post post) {
        return user == null || user.getNickname() != post.getUser().getNickname();
    }

    public List<CommentDto.Response> getCommentDtos(Post post) {
        return post.getComments().stream().map(comment -> CommentDto.Response.fromEntity(comment)).collect(Collectors.toList());
    }

    public Set<TagDto> getTagDtos(Post post) {
        return post.getTags().stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toSet());
    }

    private Post findByTypeAndId(int boardId, Long postId) {
        return postRepository.findByIdAndType(postId, boardId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Long updatePost(User user, int boardId, Long postId, PostDto.Request postRequestDto) {

        Post findPost = findByTypeAndId(boardId, postId);

        Set<Tag> tags = getTags(postRequestDto);

        validateAccount(user, findPost);

        return findPost.updatePost(postRequestDto, tags);
    }

    private void validateAccount(User user, Post findPost) {
        if (!isEqualsAccount(user, findPost)) {
            throw new PostException(PostErrorCode.NOT_ACCORD_ACCOUNT);
        }
    }

    private boolean isEqualsAccount(User user, Post findPost) {
        return findPost.getUser().equals(user);
    }


    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(User user, int boardId, Long postId) {
        Post findPost = findByTypeAndId(boardId, postId);
        validateAccount(user, findPost);
        postRepository.delete(findPost);

        //TODO 이미지 서버에서 삭제
    }


    /**
     * 전체 게시글 조회
     */
    public List<PostDto.ResponseAll> findAllPost(int boardId) {

        List<Post> posts = postRepository.findAllByType(boardId);


        return posts.stream()
                .map(post ->
                        PostDto.ResponseAll.fromEntity(
                                post,
                                post.getTags().stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public Post findByPostId(Long postsId) {
        return postRepository.findById(postsId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
    }

    public List<PostDto.ResponseAll> findAllPostOrderByLike(Pageable pageable) {
        Slice<Post> postSlice = postRepository.findPostsAllOrderByLike(pageable);

        List<Post> content = postSlice.getContent();

        return content.stream()
                .map(post ->
                        PostDto.ResponseAll.fromEntity(
                                post,
                                post.getTags().stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }
}
