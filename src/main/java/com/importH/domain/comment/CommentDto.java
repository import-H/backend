package com.importH.domain.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;


@ApiModel("댓글 DTO")
public class CommentDto {


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class Request {

        @ApiModelProperty(value = "댓글 내용", example = "댓글 입니다.")
        private String content;

        public Comment toEntity() {
            return Comment.builder()
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Response {

        @ApiModelProperty(value = "댓글 ID", example = "1")
        private Long id;

        @ApiModelProperty(value = "댓글 작성자", example = "닉네임1")
        private String nickname;

        @ApiModelProperty(value = "프로필 이미지",example = "http://localhost:8090/v1/user/profileImage")
        private String profileImageUrl;

        @ApiModelProperty(value = "댓글 내용", example = "댓글 입니다.")
        private String content;

        @ApiModelProperty(value = "댓글 작성 시간")
        private LocalDateTime createdAt;



        public static Response fromEntity(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .nickname(comment.getUser().getNickname())
                    .profileImageUrl(comment.getUser().getProfileImage())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }

}
