package com.importH.core.domain.post;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.base.BaseTimeEntity;
import com.importH.core.domain.file.File;
import com.importH.core.domain.tag.Tag;
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

}
