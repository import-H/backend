package com.importH.domain.post.service;


import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostLike;
import com.importH.domain.post.repository.PostLikeRepository;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    /**
     * 게시글 좋아요 요청
     */
    @Transactional
    public void addLike(User user, Long postId) {

        Post post = postRepository.findPostWithLikeById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_FOUND_POST));

        Optional<PostLike> findPostLike = post.getLikes().stream().filter(postLike -> postLike.getUser().equals(user)).findFirst();


        if (findPostLike.isEmpty()) {
            increaseLike(user, post);
        }
    }

    /**
     * 게시글 좋아요 취소
     */
    @Transactional
    public void cancelLike(User user, Long postId) {

        Post post = postRepository.findPostWithLikeById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_FOUND_POST));

        Optional<PostLike> findPostLike = post.getLikes().stream().filter(postLike -> postLike.getUser().equals(user)).findFirst();

        if (findPostLike.isPresent()) {
            decreaseLike(post, findPostLike.get());
        }
    }


    private void increaseLike(User user, Post post) {
        PostLike postLike = savePostLike(PostLike.builder().post(post).user(user).build());
        post.addLike(postLike);
    }

    private PostLike savePostLike(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    private void decreaseLike(Post post, PostLike postLike) {
        deletePostLike(postLike);
        post.deleteLike(postLike);
    }

    private void deletePostLike(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    /**
     * 좋아요 한 게시글 가져오기
     */

    public List<UserPostDto.Response> findAllPostLike(User user, Pageable pageable) {
        Page<UserPostDto.Response> responses = postLikeRepository.findAllByUser(user, pageable);
        return responses.getContent();
    }
}
