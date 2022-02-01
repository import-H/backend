package com.importH.core.dto.tag;

import com.importH.core.domain.tag.Tag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto {

    private String name;

    public Tag toEntity() {
        return Tag.builder().title(name).build();
    }
}
