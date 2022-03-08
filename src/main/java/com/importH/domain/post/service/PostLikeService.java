package com.importH.domain.post.service;


import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostLike;
import com.importH.domain.post.repository.PostLikeRepository;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void changeLike(User user, Long postId) {

        Post post = postRepository.findPostWithLikeById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_FOUND_POST));

        Optional<PostLike> findPostLike = post.getLikes().stream().filter(postLike -> postLike.getUser().equals(user)).findFirst();

        if (findPostLike.isPresent()) {
            decreaseLike(post, findPostLike.get());
            return;
        }
            increaseLike(user, post);
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
}