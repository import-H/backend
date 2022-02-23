package com.importH.domain.tag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "name")
@ToString(of = {"name"})
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
