package com.importH.core.service;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.comment.Comment;
import com.importH.core.domain.comment.CommentRepository;
import com.importH.core.domain.post.Post;
import com.importH.core.dto.post.CommentDto;
import com.importH.core.error.code.CommentErrorCode;
import com.importH.core.error.exception.CommentException;
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

    /**
     * 댓글 수정
     */
    //TODO 조회 성능최적화
    @Transactional
    public void updateComment(Long postsId, Long commentId, Account account, CommentDto.Request commentDto) {
        Post post = postService.findByPostId(postsId);
        Comment comment = findByCommentId(commentId);

        isUpdatableComment(account, post, comment);

        comment.updateComment(commentDto);
    }

    private void isUpdatableComment(Account account, Post post, Comment comment) {
        if (!isEqualsAccount(account, comment)) {
            throw new CommentException(CommentErrorCode.NOT_AUTHORITY);
        }
        if (!isEqualsPost(post, comment)) {
            throw new CommentException(CommentErrorCode.NOT_EQUALS_POST);
        }
    }

    private boolean isEqualsPost(Post post, Comment comment) {
        return comment.getPost().equals(post);
    }

    private boolean isEqualsAccount(Account account, Comment comment) {
        return comment.getAccount().equals(account);
    }

    private Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentException(CommentErrorCode.NOT_FOUND));
    }
}
