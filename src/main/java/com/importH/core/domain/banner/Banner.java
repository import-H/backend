package com.importH.core.domain.banner;

import com.importH.core.domain.tag.Tag;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Banner {

    @Id @GeneratedValue
    @Column(name = "banner_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToMany
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();


    public String getStoreImageUrl() {
        return imageUrl.substring(imageUrl.indexOf("upload")+7);
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
