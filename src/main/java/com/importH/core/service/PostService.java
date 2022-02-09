package com.importH.core.service;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostLike;
import com.importH.core.domain.post.PostLikeRepository;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.post.CommentDto;
import com.importH.core.dto.post.PostDto;
import com.importH.core.dto.tag.TagDto;
import com.importH.core.error.code.PostErrorCode;
import com.importH.core.error.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /**
     * 게시글 저장
     */
    @Transactional
    public Post registerPost(Account account, int boardType, PostDto.Request postRequestDto) {

        Post post = postRequestDto.toEntity();

        setPostRelation(account, boardType, postRequestDto, post);

        return savePost(post);
    }

    private void setPostRelation(Account account, int boardType, PostDto.Request postRequestDto, Post post) {
        post.setTags(getTags(postRequestDto));
        post.setAccount(account);
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
    public PostDto.Response getPost(Account account, int boardId, Long postId) {

        Post post = findByTypeAndId(boardId, postId);

        increaseViewCount(account, post);

        Set<TagDto> tags = getTagDtos(post);
        List<CommentDto.Response> comments = getCommentDtos(post);

        boolean isLike = havePostLike(account, post);
        //TODO 계정 삭제시에 조회 어떻게 처리할것인지 생각

        return PostDto.Response.fromEntity(post, tags, comments, isLike);
    }

    private boolean havePostLike(Account account, Post post) {
        return postLikeRepository.existsByAccountAndPost(account, post);
    }

    private void increaseViewCount(Account account, Post post) {
        if (isNotAuthor(account, post)) {
            post.increaseView();
        }
    }

    private boolean isNotAuthor(Account account, Post post) {
        return account == null || account.getNickname() != post.getAccount().getNickname();
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
    public Long updatePost(Account account, int boardId, Long postId, PostDto.Request postRequestDto) {

        Post findPost = findByTypeAndId(boardId, postId);

        Set<Tag> tags = getTags(postRequestDto);

        validateAccount(account, findPost);

        return findPost.updatePost(postRequestDto, tags);
    }

    private void validateAccount(Account account, Post findPost) {
        if (!isEqualsAccount(account, findPost)) {
            throw new PostException(PostErrorCode.NOT_ACCORD_ACCOUNT);
        }
    }

    private boolean isEqualsAccount(Account account, Post findPost) {
        return findPost.getAccount().equals(account);
    }


    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Account account, int boardId, Long postId) {
        Post findPost = findByTypeAndId(boardId, postId);
        validateAccount(account, findPost);
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
}
