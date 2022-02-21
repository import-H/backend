package com.importH.core.domain.image;

import com.importH.core.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id @GeneratedValue
    private Long id;

    private String uploadFileName;

    private String storeFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void setPost(Post post) {
        this.post = post;
    }

}
