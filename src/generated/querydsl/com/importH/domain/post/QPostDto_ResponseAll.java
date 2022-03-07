package com.importH.domain.post;

import com.importH.domain.post.dto.PostDto;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.importH.domain.post.QPostDto_ResponseAll is a Querydsl Projection type for ResponseAll
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPostDto_ResponseAll extends ConstructorExpression<PostDto.ResponseAll> {

    private static final long serialVersionUID = -195333483L;

    public QPostDto_ResponseAll(com.querydsl.core.types.Expression<? extends PostDto.ResponseInfo> responseInfo, com.querydsl.core.types.Expression<Integer> commentsCount, com.querydsl.core.types.Expression<String> thumbnail) {
        super(PostDto.ResponseAll.class, new Class<?>[]{PostDto.ResponseInfo.class, int.class, String.class}, responseInfo, commentsCount, thumbnail);
    }

}

