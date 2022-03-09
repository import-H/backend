package com.importH.domain.post.service;

import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostScrap;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.post.repository.PostScrapRepository;
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
public class PostScrapService {

    private final PostRepository postRepository;

    private final PostScrapRepository postScrapRepository;
    /**
     * 게시글 스크랩 하기
     */
    @Transactional
    public void scrap(Long postId, User currentUser) {
        Post post = findPost(postId);

        Optional<PostScrap> scrap = findPostScrapByUser(currentUser, post);

        if(scrap.isEmpty())
        addScrap(currentUser, post);

    }

    private void addScrap(User currentUser, Post post) {
        PostScrap postScrap = postScrapRepository.save(PostScrap.create(post, currentUser));
        post.addScrap(postScrap);
    }

    private void removeScrap(Post post, PostScrap postScrap) {
        postScrapRepository.delete(postScrap);
        post.removeScrap(postScrap);
    }

    private Optional<PostScrap> findPostScrapByUser(User currentUser, Post post) {
        return post.getScraps().stream()
                .filter(postScrap -> postScrap.getUser().equals(currentUser))
                .findFirst();
    }

    private Post findPost(Long postId) {
        return postRepository
                .findPostWithScrapById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.NOT_FOUND_POST));
    }

    /**
     * 해당 유저 스크랩 가져오기
     */
    public List<UserPostDto.Response> findAllScrap(User user, Pageable pageable) {
        Page<UserPostDto.Response> responses = postScrapRepository.findAllByUser(user, pageable);
        return responses.getContent();
    }

    /**
     * 스크랩 취소하기
     */
    @Transactional
    public void cancelScrap(Long postId, User user) {

        Post post = findPost(postId);

        findPostScrapByUser(user, post).ifPresent(scrap -> removeScrap(post, scrap));

    }
}
