package com.importH.core.dto.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.dto.tag.TagDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApiModel("전체 게시글 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAllResponseDto {

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

    @ApiModelProperty(value = "댓글 수", example = "11")
    private int commentsCount;

    @ApiModelProperty(value = "작성시간", example = "yyyy-MM-dd/HH:mm")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "썸네일", example = "")
    private String thumbnail;


    @Builder
    public PostAllResponseDto(Long id, String title, Set<TagDto> tags, String content, String author, int viewCount, int likeCount, int commentsCount, LocalDateTime createdAt, String thumbnail) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.content = content;
        this.author = author;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
        this.createdAt = createdAt;
        this.thumbnail = thumbnail;
    }

    public static PostAllResponseDto fromEntity(Post post, Account account, Set<TagDto> tags) {

        return PostAllResponseDto.builder()
                .id(post.getId())
                .author(account.getNickName())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .title(post.getTitle())
                .tags(tags)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentsCount(post.getComments().size())
                .thumbnail(null) // TODO 썸네일 작업
                .build();
    }

}
