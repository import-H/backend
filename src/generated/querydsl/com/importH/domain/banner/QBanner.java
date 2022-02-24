package com.importH.domain.banner;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBanner is a Querydsl query type for Banner
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBanner extends EntityPathBase<Banner> {

    private static final long serialVersionUID = 866389436L;

    public static final QBanner banner = new QBanner("banner");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath nickname = createString("nickname");

    public final SetPath<com.importH.domain.tag.Tag, com.importH.domain.tag.QTag> tags = this.<com.importH.domain.tag.Tag, com.importH.domain.tag.QTag>createSet("tags", com.importH.domain.tag.Tag.class, com.importH.domain.tag.QTag.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final StringPath url = createString("url");

    public QBanner(String variable) {
        super(Banner.class, forVariable(variable));
    }

    public QBanner(Path<? extends Banner> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBanner(PathMetadata metadata) {
        super(Banner.class, metadata);
    }

}

