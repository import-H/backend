package com.importH.core.service;


import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostLike;
import com.importH.core.domain.post.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    /**
     * 게시글 좋아요 요청
     */
    @Transactional
    public void changeLike(Account account, Long postId) {

        Post post = postService.findByPostId(postId);

        Optional<PostLike> postLike = postLikeRepository.findByPostAndAccount(post, account);

        if (postLike.isPresent()) {
            decreaseLike(post, postLike.get());
            return;
        }
            increaseLike(account, post);
    }

    private void increaseLike(Account account, Post post) {
        PostLike postLike = savePostLike(PostLike.builder().post(post).account(account).build());
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