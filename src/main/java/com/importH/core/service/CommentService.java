package com.importH.core.service;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.comment.CommentRepository;
import com.importH.core.domain.post.Post;
import com.importH.core.dto.post.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    /**
     * 댓글 등록
     */
    @Transactional
    public void registerComment(Long postsId, Account account, CommentDto.Request commentDto) {
        Post post = postService.findByPostId(postsId);
        saveComment(account, commentDto, post);
    }

    private void saveComment(Account account, CommentDto.Request commentDto, Post post) {
        commentRepository.save(commentDto.toEntity(account, post));
    }
}
