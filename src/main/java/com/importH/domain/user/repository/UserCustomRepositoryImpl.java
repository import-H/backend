package com.importH.domain.user.repository;

import com.importH.domain.user.entity.QUser;
import com.importH.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.util.List;



public class UserCustomRepositoryImpl implements UserCustomRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserCustomRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<User> findAllUsers(Pageable pageable) {
        QUser user = QUser.user;
        List<User> users = queryFactory.selectFrom(user)
                .where(user.emailVerified.isTrue())
                .orderBy(user.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = false;
        if (users.size() > pageable.getPageSize()) {
            users.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl(users,pageable,hasNext);
    }
}
