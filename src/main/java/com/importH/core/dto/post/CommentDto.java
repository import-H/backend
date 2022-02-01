package com.importH.core.dto.post;

import com.importH.core.domain.comment.Comment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel("댓글 DTO")
public class CommentDto {

    @ApiModelProperty(value = "댓글 내용", example = "댓글 입니다.")
    private String content;


    @Builder
    public CommentDto(String content) {
        this.content = content;
    }

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .content(comment.getContent())
                .build();
    }

}
