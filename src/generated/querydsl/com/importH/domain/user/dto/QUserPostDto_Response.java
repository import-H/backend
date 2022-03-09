package com.importH.domain.user.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.importH.domain.user.dto.QUserPostDto_Response is a Querydsl Projection type for Response
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QUserPostDto_Response extends ConstructorExpression<UserPostDto.Response> {

    private static final long serialVersionUID = -272826053L;

    public QUserPostDto_Response(com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt, com.querydsl.core.types.Expression<String> author, com.querydsl.core.types.Expression<String> profileImage, com.querydsl.core.types.Expression<String> postUri) {
        super(UserPostDto.Response.class, new Class<?>[]{String.class, java.time.LocalDateTime.class, String.class, String.class, String.class}, title, createdAt, author, profileImage, postUri);
    }

}

