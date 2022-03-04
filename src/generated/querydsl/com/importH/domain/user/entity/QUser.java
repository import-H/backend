package com.importH.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1246809787L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.importH.domain.QBaseTimeEntity _super = new com.importH.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath deleted = createBoolean("deleted");

    public final DateTimePath<java.time.LocalDateTime> deletedTime = createDateTime("deletedTime", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final StringPath emailCheckToken = createString("emailCheckToken");

    public final DateTimePath<java.time.LocalDateTime> emailCheckTokenGeneratedAt = createDateTime("emailCheckTokenGeneratedAt", java.time.LocalDateTime.class);

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInfoAgree infoAgree;

    public final StringPath introduction = createString("introduction");

    public final StringPath nickname = createString("nickname");

    public final StringPath oauthId = createString("oauthId");

    public final StringPath password = createString("password");

    public final StringPath pathId = createString("pathId");

    public final StringPath personalUrl = createString("personalUrl");

    public final StringPath profileImage = createString("profileImage");

    public final com.importH.domain.user.token.QRefreshToken refreshToken;

    public final StringPath role = createString("role");

    public final SetPath<com.importH.domain.tag.Tag, com.importH.domain.tag.QTag> tags = this.<com.importH.domain.tag.Tag, com.importH.domain.tag.QTag>createSet("tags", com.importH.domain.tag.Tag.class, com.importH.domain.tag.QTag.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final BooleanPath weekAgree = createBoolean("weekAgree");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.infoAgree = inits.isInitialized("infoAgree") ? new QInfoAgree(forProperty("infoAgree")) : null;
        this.refreshToken = inits.isInitialized("refreshToken") ? new com.importH.domain.user.token.QRefreshToken(forProperty("refreshToken"), inits.get("refreshToken")) : null;
    }

}

