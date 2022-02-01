package com.importH.core.dto.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.file.File;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.tag.TagDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRequestDto {


    @NotBlank(message = "제목은 필수 입력값 입니다.")
    private String title;

    private List<TagDto> tags;

    @NotBlank(message = "내용은 필수 입력값 입니다.")
    private String content;


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
