package com.importH.domain.user.dto;

import com.importH.domain.post.entity.Post;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@ApiModel(value = "유저 게시글 DTO (스크랩,좋아요,작성한게시글)")
public class UserPostDto {

    @Getter
    @ApiModel(value = "유저 스크랩 DTO")
    public static class Response {

        @ApiModelProperty(value = "게시글 제목", example = "샘플 포스트")
        private String title;

        @ApiModelProperty(value = "작성시간", example = "yyyy-MM-dd/HH:mm")
        private LocalDateTime createdAt;

        @ApiModelProperty(value = "작성자", example = "닉네임")
        private String author;

        @ApiModelProperty(value = "작성자 프로필 이미지 주소", example = "http://localhost:8090/v1/profile/...")
        private String profileImage;

        @ApiModelProperty(value = "게시판타입/게시글 번호", example = "free/3")
        private String postUri;

        public static Response fromEntity(Post post) {
            return Response.builder()
                    .author(post.getUser().getNickname())
                    .profileImage(post.getUser().getProfileImage())
                    .postUri(post.getType() + "/" + post.getId())
                    .createdAt(post.getCreatedAt())
                    .title(post.getTitle())
                    .build();

        }


        @QueryProjection
        @Builder
        public Response(String title, LocalDateTime createdAt, String author, String profileImage, String postUri) {
            this.title = title;
            this.createdAt = createdAt;
            this.author = author;
            this.profileImage = profileImage;
            this.postUri = postUri;
        }
    }
}
