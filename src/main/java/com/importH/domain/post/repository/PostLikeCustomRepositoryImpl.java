package com.importH.domain.post.repository;

import com.importH.domain.user.dto.QUserPostDto_Response;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.importH.domain.post.entity.QPostLike.postLike;

public class PostLikeCustomRepositoryImpl implements PostLikeCustomRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public PostLikeCustomRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<UserPostDto.Response> findAllByUser(User currentUser, Pageable pageable) {



        List<UserPostDto.Response> likes = queryFactory
                .select(new QUserPostDto_Response(
                        postLike.post.title
                        , postLike.post.createdAt
                        , postLike.post.user.nickname
                        , postLike.post.user.profileImage
                        , postLike.post.type.append("/").append(postLike.post.id.stringValue())))
                .from(postLike)
                .where(postLike.user.eq(currentUser))
                .orderBy(postLike.post.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();


        return new PageImpl<>(likes, pageable, likes.size());
    }
}
