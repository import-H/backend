package com.importH.core.domain.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.base.BaseTimeEntity;
import com.importH.core.domain.comment.Comment;
import com.importH.core.domain.file.File;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    private int viewCount;

    private int likeCount;

    private int type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Long updatePost(PostDto.Request postRequestDto, Set<Tag> tags) {
        this.tags = tags;
        this.content = postRequestDto.getContent();
        this.title = postRequestDto.getTitle();
        return id;
    }
}
