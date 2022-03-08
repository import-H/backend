package com.importH.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 495776623L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.importH.domain.QBaseTimeEntity _super = new com.importH.domain.QBaseTimeEntity(this);

    public final SetPath<com.importH.domain.comment.Comment, com.importH.domain.comment.QComment> comments = this.<com.importH.domain.comment.Comment, com.importH.domain.comment.QComment>createSet("comments", com.importH.domain.comment.Comment.class, com.importH.domain.comment.QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.importH.domain.image.Image, com.importH.domain.image.QImage> images = this.<com.importH.domain.image.Image, com.importH.domain.image.QImage>createList("images", com.importH.domain.image.Image.class, com.importH.domain.image.QImage.class, PathInits.DIRECT2);

    public final BooleanPath important = createBoolean("important");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final SetPath<PostLike, QPostLike> likes = this.<PostLike, QPostLike>createSet("likes", PostLike.class, QPostLike.class, PathInits.DIRECT2);

    public final SetPath<PostScrap, QPostScrap> scraps = this.<PostScrap, QPostScrap>createSet("scraps", PostScrap.class, QPostScrap.class, PathInits.DIRECT2);

    public final SetPath<com.importH.domain.tag.Tag, com.importH.domain.tag.QTag> tags = this.<com.importH.domain.tag.Tag, com.importH.domain.tag.QTag>createSet("tags", com.importH.domain.tag.Tag.class, com.importH.domain.tag.QTag.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final StringPath type = createString("type");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.importH.domain.user.entity.QUser user;

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.importH.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

