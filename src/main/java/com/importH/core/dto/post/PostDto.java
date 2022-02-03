package com.importH.core.dto.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.file.File;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.tag.TagDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        @ApiModelProperty(value = "이미지", example = "image")
        private List<MultipartFile> imageFiles = new ArrayList<>();


        public Post toEntity(Account account, List<File> files, Set<Tag> tags, int type) {
            return Post.builder()
                    .account(account)
                    .type(type)
                    .files(files)
                    .content(content)
                    .title(title)
                    .tags(tags).build();
        }
    }

    @Getter
    @Builder
    public static class ResponseInfo {
        @ApiModelProperty(value = "게시글 id", example = "1")
        private Long id;

        @ApiModelProperty(value = "게시글 제목", example = "샘플 포스트")
        private String title;

        @ApiModelProperty(value = "태그 정보", example = "[aa,bb]")
        private Set<TagDto> tags = new HashSet<>();

        @ApiModelProperty(value = "게시글 내용", example = "샘플 포스트 입니다.")
        private String content;

        @ApiModelProperty(value = "글쓴이", example = "글쓴이")
        private String author;

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

        @ApiModelProperty(value = "댓글", example = "[댓글1,댓글2]")
        private List<CommentDto> comments = new ArrayList<>();


        public static Response fromEntity(Post post, Account account, Set<TagDto> tags, List<CommentDto> comments) {
            return Response.builder()
                    .responseInfo(ResponseInfo.builder()
                            .id(post.getId())
                            .author(account.getNickName())
                            .content(post.getContent())
                            .createdAt(post.getCreatedAt())
                            .title(post.getTitle())
                            .tags(tags)
                            .viewCount(post.getViewCount())
                            .likeCount(post.getLikeCount())
                            .build())
                    .comments(comments)
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
        public static ResponseAll fromEntity(Post post, Account account, Set<TagDto> tags) {

            return ResponseAll.builder()
                    .responseInfo(ResponseInfo.builder()
                            .id(post.getId())
                            .author(account.getNickName())
                            .content(post.getContent())
                            .createdAt(post.getCreatedAt())
                            .title(post.getTitle())
                            .tags(tags)
                            .viewCount(post.getViewCount())
                            .likeCount(post.getLikeCount())
                            .build())
                    .commentsCount(post.getComments().size())
                    .thumbnail(null) // TODO 썸네일 작업
                    .build();
        }
    }






}
