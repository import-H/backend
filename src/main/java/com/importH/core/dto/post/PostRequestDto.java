package com.importH.core.dto.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.file.File;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.tag.TagDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApiModel("게시글 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRequestDto {


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


    @Builder
    public PostRequestDto(String title, List<TagDto> tags, String content) {
        this.title = title;
        this.tags = tags;
        this.content = content;
    }


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
