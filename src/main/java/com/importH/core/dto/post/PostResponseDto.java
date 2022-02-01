package com.importH.core.dto.post;

import com.importH.core.dto.tag.TagDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApiModel("게시글 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponseDto {

    @ApiModelProperty(value = "게시글 id" , example = "1")
    private String id;

    @ApiModelProperty(value = "게시글 제목" , example = "샘플 포스트")
    private String title;

    private List<TagDto> tags = new ArrayList<>();

    @ApiModelProperty(value = "게시글 내용" , example = "샘플 포스트 입니다.")
    private String content;

    @ApiModelProperty(value = "글쓴이" , example = "글쓴이")
    private String author;

    @ApiModelProperty(value = "조회수" , example = "10")
    private int viewCount;

    @ApiModelProperty(value = "좋아요수" , example = "10")
    private int likeCount;

    @ApiModelProperty(value = "작성시간" , example = "yyyy-MM-dd/HH:mm")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "썸네일" , example = "https://images.velog.io/images/wkahd01/post/b3541fd3-4a65-4d28-b9d6-e31238b09ba2/%EC%BA%A1%EC%B2%98.PNG")
    private int thumbnail;

}
