package com.importH.domain.post.service;

import com.importH.domain.comment.CommentDto;
import com.importH.domain.image.FileService;
import com.importH.domain.post.dto.PostDto;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostType;
import com.importH.domain.post.repository.PostLikeRepository;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.tag.Tag;
import com.importH.domain.tag.TagDto;
import com.importH.domain.tag.TagService;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.exception.PostException;
import com.importH.global.error.exception.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.importH.global.error.code.PostErrorCode.NOT_FOUND_POST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final PostLikeRepository postLikeRepository;

    private final FileService fileService;

    /**
     * 게시글 저장
     */
    @Transactional
    public Post registerPost(User user, PostDto.Request postRequestDto) {

        Post post = postRequestDto.toEntity();

        validateType(user,post);
        setPostRelation(user, postRequestDto, post);

        return savePost(post);
    }

    private void validateType(User user, Post post) {
        if (isValidPathId(user, post) || isExistType(post.getType())) return;
        throw new PostException(PostErrorCode.NOT_EXIST_TYPE);
    }

    private boolean isExistType(String type) {
        return Arrays.stream(PostType.values())
                .anyMatch(postType -> postType.getType().equals(type));
    }

    private boolean isValidPathId(User user, Post post) {
        if (user.getPathId() != null && user.getPathId().equals(post.getType())) {
            return true;
        }
        return false;
    }

    private void setPostRelation(User user, PostDto.Request postRequestDto, Post post) {
        post.setTags(tagService.getTags(postRequestDto.getTags()));
        post.setUser(user);
        post.addImage(fileService.getPostImages(postRequestDto.getImages()));
    }


    private Post savePost(Post post) {
        Post save = postRepository.save(post);

        return save;
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostDto.Response getPost(User user, Long postId) {

        Post post = findWithAll(postId);

        increaseViewCount(user, post);

        Set<TagDto> tags = tagService.getTagDtos(post.getTags());
        List<CommentDto.Response> comments = getCommentDtos(post);

        boolean isLike = havePostLike(user, post);
        boolean isScrap = haveScrap(user, post);

        return PostDto.Response.fromEntity(post, tags, comments, isLike,isScrap);
    }

    private boolean haveScrap(User user, Post post) {
        return post.getScraps().stream().anyMatch(postScrap -> postScrap.getUser().equals(user));
    }

    public Post findWithAll(Long postsId) {
        return postRepository.findWithAllById(postsId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
    }

    private boolean havePostLike(User user, Post post) {
        return  post.getLikes().stream().anyMatch(postLike -> postLike.getUser().equals(user));
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

    /**
     * 게시글 수정
     */
    @Transactional
    public Long updatePost(User user, Long postId, PostDto.Request postRequestDto) {

        Post findPost = findByPostId(postId);

        Set<Tag> tags = tagService.getTags(postRequestDto.getTags());

        //TODO 없어진 이미지들 삭제
//        postRequestDto.getImages()

        validateAccount(user, findPost);

        return findPost.updatePost(postRequestDto, tags);
    }

    private void validateAccount(User user, Post findPost) {
        if (!isEqualsAccount(user, findPost)) {
            throw new SecurityException(SecurityErrorCode.ACCESS_DENIED);
        }
    }

    private boolean isEqualsAccount(User user, Post findPost) {
        return findPost.getUser().equals(user);
    }


    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(User user, Long postId) {
        Post findPost = findByPostId(postId);
        validateAccount(user, findPost);

        fileService.deletePostImages(findPost);
        postRepository.delete(findPost);
    }


    /**
     * 전체 게시글 조회
     */
    public List<PostDto.ResponseAll> findAllPost(String type, Pageable pageable) {

        List<Post> importantIsTrue = new ArrayList<>();
        if (isExistType(type)) {
            importantIsTrue = postRepository.findAllByImportantIsTrue();
        }

        Slice<Post> postSlice = postRepository.findAllPostsByType(type,pageable);

        List<Post> content = postSlice.getContent();

        return Stream.of(importantIsTrue,content)
                .flatMap(posts -> posts.stream())
                .map(PostDto.ResponseAll::fromEntity)
                .collect(Collectors.toList());
    }

    public Post findByPostId(Long postsId) {
        return postRepository.findById(postsId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
    }

    /**
     * 유저가 작성한 게시글 가져오기
     */
    public List<UserPostDto.Response> findAllPostByWrote(User user, Pageable pageable) {
        Page<UserPostDto.Response> responses = postRepository.findAllPostByUser(user, pageable);
        return responses.getContent();
    }
}
