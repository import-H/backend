package com.importH.domain.post.repository;

import com.importH.domain.post.dto.QScrapDto_Response;
import com.importH.domain.post.dto.ScrapDto;
import com.importH.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.importH.domain.post.entity.QPostScrap.postScrap;

public class PostScrapCustomRepositoryImpl implements PostScrapCustomRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public PostScrapCustomRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ScrapDto.Response> findAllByUser(User currentUser, Pageable pageable) {



        List<ScrapDto.Response> scraps = queryFactory
                .select(new QScrapDto_Response(
                        postScrap.post.title
                        , postScrap.post.createdAt
                        , postScrap.post.user.nickname
                        , postScrap.post.user.profileImage
                        , postScrap.post.type.append("/").append(postScrap.post.id.stringValue())))
                .from(postScrap)
                .where(postScrap.user.eq(currentUser))
                .orderBy(postScrap.post.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();


        return new PageImpl<>(scraps, pageable, scraps.size());
    }
}
