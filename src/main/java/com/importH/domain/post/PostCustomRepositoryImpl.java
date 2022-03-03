package com.importH.domain.post;

import com.importH.domain.user.entity.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    QPost post = QPost.post;
    QUser user = QUser.user;

    public PostCustomRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    //todo 커버링 인덱스 적용
    @Override
    public Slice<Post> findAllPostsByType(String type, Pageable pageable) {
        List<Post> posts = queryFactory
                .select(post)
                .from(post)
                .where(typeEq(type))
                .leftJoin(post.user, user).fetchJoin()
                .orderBy(getAllOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();



        boolean hasNext = false;
        if (posts.size() > pageable.getPageSize()) {
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl(posts,pageable,hasNext);
    }

    private BooleanExpression typeEq(String boardId) {
        if (StringUtils.isNullOrEmpty(boardId)) {
            return null;
        }
        return post.type.eq(boardId);
    }

    private OrderSpecifier[] getAllOrderSpecifiers(Pageable pageable) {

        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                OrderSpecifier<?> orderCategory = getSortedColumn(direction, post, order.getProperty());

                ORDERS.add(orderCategory);
            }
        }

        return ORDERS.stream().toArray(OrderSpecifier[]::new);
    }

    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
        return new OrderSpecifier(order, fieldPath);
    }

}
