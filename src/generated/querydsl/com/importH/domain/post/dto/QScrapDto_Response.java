package com.importH.domain.post.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.importH.domain.post.dto.QScrapDto_Response is a Querydsl Projection type for Response
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QScrapDto_Response extends ConstructorExpression<ScrapDto.Response> {

    private static final long serialVersionUID = 1825623574L;

    public QScrapDto_Response(com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt, com.querydsl.core.types.Expression<String> author, com.querydsl.core.types.Expression<String> profileImage, com.querydsl.core.types.Expression<String> postUri) {
        super(ScrapDto.Response.class, new Class<?>[]{String.class, java.time.LocalDateTime.class, String.class, String.class, String.class}, title, createdAt, author, profileImage, postUri);
    }

}

