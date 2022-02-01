package com.importH.core.dto.tag;

import com.importH.core.domain.tag.Tag;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagDto {

    private String name;

    @Builder
    public TagDto(String name) {
        this.name = name;
    }

    public Tag toEntity() {
        return Tag.builder().name(name).build();
    }


    public static TagDto fromEntity(Tag tag) {
        return TagDto.builder().name(tag.getName()).build();
    }
}
