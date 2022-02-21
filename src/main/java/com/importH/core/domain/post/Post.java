package com.importH.core.domain.post;

import com.importH.core.domain.comment.Comment;
import com.importH.core.domain.image.Image;
import com.importH.core.domain.tag.Tag;
import com.importH.core.domain.user.User;
import com.importH.core.dto.post.PostDto;
import com.importH.core.model.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();


    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public Long updatePost(PostDto.Request postRequestDto, Set<Tag> tags) {
        this.tags = tags;
        this.content = postRequestDto.getContent();
        this.title = postRequestDto.getTitle();
        return id;
    }

    public void increaseView() {
        this.viewCount++;
    }

    public void addLike(PostLike like) {
        this.postLikes.add(like);
        this.likeCount++;
    }

    public void deleteLike(PostLike like) {
        this.postLikes.remove(like);
        this.likeCount--;
    }

    public void addComment(Comment comment) {
        comment.setPost(this);
        this.getComments().add(comment);
    }

    public void deleteComment(Comment comment) {
        this.getComments().remove(comment);
    }

    public void setUser(User user) {
        this.user = user;
    }


    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addImage(List<Image> images) {
        this.images = images;
        images.stream().forEach(image -> image.setPost(this));
    }
}
