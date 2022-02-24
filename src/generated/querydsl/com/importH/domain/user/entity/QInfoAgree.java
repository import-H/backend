package com.importH.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInfoAgree is a Querydsl query type for InfoAgree
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QInfoAgree extends BeanPath<InfoAgree> {

    private static final long serialVersionUID = -875105948L;

    public static final QInfoAgree infoAgree = new QInfoAgree("infoAgree");

    public final BooleanPath infoByEmail = createBoolean("infoByEmail");

    public final BooleanPath infoByWeb = createBoolean("infoByWeb");

    public QInfoAgree(String variable) {
        super(InfoAgree.class, forVariable(variable));
    }

    public QInfoAgree(Path<? extends InfoAgree> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInfoAgree(PathMetadata metadata) {
        super(InfoAgree.class, metadata);
    }

}

