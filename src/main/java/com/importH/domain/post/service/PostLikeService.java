package com.importH.domain.post.service;


import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostLike;
import com.importH.domain.post.repository.PostLikeRepository;
import com.importH.domain.user.entity.User;
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
    public void changeLike(User user, Long postId) {

        Post post = postService.findByPostId(postId);


        //TODO postLikeRepository 가 아닌 post 로 조회
        Optional<PostLike> postLike = postLikeRepository.findByPostAndUser(post, user);

        if (postLike.isPresent()) {
            decreaseLike(post, postLike.get());
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
