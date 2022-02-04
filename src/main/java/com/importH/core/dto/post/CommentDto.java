package com.importH.core.dto.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.comment.Comment;
import com.importH.core.domain.post.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@ApiModel("댓글 DTO")
public class CommentDto {


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class Request {

        @ApiModelProperty(value = "댓글 내용", example = "댓글 입니다.")
        private String content;

        public Comment toEntity(Account account, Post post) {
            return Comment.builder()
                    .content(content)
                    .account(account)
                    .post(post).build();
        }
    }

    @Getter
    @Builder
    public static class Response {

        @ApiModelProperty(value = "댓글 작성자", example = "닉네임1")
        private String nickname;

        @ApiModelProperty(value = "댓글 내용", example = "댓글 입니다.")
        private String content;

        public static Response fromEntity(Comment comment, Account account) {
            return Response.builder()
                    .nickname(account.getNickName())
                    .content(comment.getContent())
                    .build();
        }
    }

}
