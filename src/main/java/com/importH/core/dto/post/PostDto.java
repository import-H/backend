package com.importH.core.dto.post;

import com.importH.core.domain.post.Post;
import com.importH.core.dto.tag.TagDto;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApiModel(value = "게시글 DTO")
public class PostDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ApiModel(value = "게시글 요청 DTO")
    public static class Request {

        @ApiModelProperty(value = "게시글 제목", example = "테스트 게시글")
        @NotBlank(message = "제목은 필수 입력값 입니다.")
        private String title;

        @ApiModelProperty(value = "게시글 태그 정보")
        private List<TagDto> tags;

        @ApiModelProperty(value = "내용", example = "테스트 게시글 입니다.")
        @NotBlank(message = "내용은 필수 입력값 입니다.")
        private String content;

        @ApiModelProperty(value = "게시판 타입", example = "free/questions/notice")
        @NotBlank(message = "게시판 타입은 필수 입니다.")
        private String type;

        public Post toEntity() {
            return Post.builder()
                    .content(content)
                    .type(type)
                    .comments(new ArrayList<>())
                    .postLikes(new ArrayList<>())
                    .title(title)
                    .tags(new HashSet<>()).build();
        }

    }

    @Getter
    @Builder
    public static class ResponseInfo {

        @ApiModelProperty(value = "게시판 id", example = "free")
        private String boardId;


        @ApiModelProperty(value = "게시글 id", example = "1")
        private Long postId;

        @ApiModelProperty(value = "게시글 제목", example = "샘플 포스트")
        private String title;

        @ApiModelProperty(value = "태그 정보", example = "[aa,bb]")
        private Set<TagDto> tags;

        @ApiModelProperty(value = "게시글 내용", example = "샘플 포스트 입니다.")
        private String content;

        @ApiModelProperty(value = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(value = "프로필 이미지 주소", example = "http://localhost:8090/v1/profile/...")
        private String profileImage;

        @ApiModelProperty(value = "조회수", example = "10")
        private int viewCount;

        @ApiModelProperty(value = "좋아요수", example = "10")
        private int likeCount;

        @ApiModelProperty(value = "작성시간", example = "yyyy-MM-dd/HH:mm")
        private LocalDateTime createdAt;

    }

    @Getter
    @Builder
    @ApiModel(value = "게시글 응답 DTO")
    public static class Response {

        private ResponseInfo responseInfo;

        @ApiModelProperty(value = "댓글", example = "[{닉네임1,댓글1},{닉네임2,댓글2}]")
        private List<CommentDto.Response> comments;

        @ApiModelProperty(value = "현재 유저 좋아요 여부", example = "true/false")
        private boolean isLike;

        public static Response fromEntity(Post post, Set<TagDto> tagDtos, List<CommentDto.Response> commentDtos, boolean isLike) {

            return Response.builder()
                    .responseInfo(ResponseInfo.builder()
                            .boardId(post.getType())
                            .postId(post.getId())
                            .nickname(post.getUser().getNickname())
                            .profileImage(post.getUser().getProfileImage())
                            .content(post.getContent())
                            .createdAt(post.getCreatedAt())
                            .title(post.getTitle())
                            .tags(tagDtos)
                            .viewCount(post.getViewCount())
                            .likeCount(post.getLikeCount())
                            .build())
                    .comments(commentDtos)
                    .isLike(isLike)
                    .build();
        }
    }

    @Getter
    @Builder
    @ApiModel(value = "전체 게시글 응답 DTO")
    public static class ResponseAll {

        private ResponseInfo responseInfo;

        @ApiModelProperty(value = "댓글 수", example = "11")
        private int commentsCount;

        @ApiModelProperty(value = "썸네일", example = "")
        private String thumbnail;

        public static ResponseAll fromEntity(Post post) {

            return ResponseAll.builder()
                    .responseInfo(ResponseInfo.builder()
                            .boardId(post.getType())
                            .postId(post.getId())
                            .nickname(post.getUser().getNickname())
                            .profileImage(post.getUser().getProfileImage())
                            .content(post.getContent())
                            .createdAt(post.getCreatedAt())
                            .title(post.getTitle())
                            .tags(post.getTags().stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toSet()))
                            .viewCount(post.getViewCount())
                            .likeCount(post.getLikeCount())
                            .build())
                    .commentsCount(post.getComments().size())
                    .thumbnail(null) // TODO 썸네일 작업
                    .build();
        }

        @QueryProjection
        public ResponseAll(ResponseInfo responseInfo, int commentsCount, String thumbnail) {
            this.responseInfo = responseInfo;
            this.commentsCount = commentsCount;
            this.thumbnail = thumbnail;
        }
    }
}
