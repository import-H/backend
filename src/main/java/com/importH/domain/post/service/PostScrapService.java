package com.importH.domain.post.service;

import com.importH.domain.post.entity.Post;
import com.importH.domain.post.entity.PostScrap;
import com.importH.domain.post.repository.PostRepository;
import com.importH.domain.post.repository.PostScrapRepository;
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
public class PostScrapService {

    private final PostRepository postRepository;

    private final PostScrapRepository postScrapRepository;
    /**
     * 게시글 스크랩 하기
     */
    @Transactional
    public void scrap(Long postId, User currentUser) {
        Post post = postRepository
                .findPostWithScrapById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.NOT_FOUND_POST));

        Optional<PostScrap> scrap = post.getScraps().stream().filter(postScrap -> postScrap.getUser().equals(currentUser)).findFirst();

        if (scrap.isPresent()) {
            postScrapRepository.delete(scrap.get());
            post.removeScrap(scrap.get());
            return;
        }

        PostScrap postScrap = postScrapRepository.save(PostScrap.create(post, currentUser));
        post.addScrap(postScrap);

    }

}
