package com.importH.core.domain.post;

import com.importH.core.domain.comment.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.util.List;

import static com.importH.core.domain.account.QAccount.account;
import static com.importH.core.domain.comment.QComment.comment;
import static com.importH.core.domain.post.QPost.post;
import static com.importH.core.domain.tag.QTag.tag;

public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public PostCustomRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<Post> findPostsAllOrderByLike(Pageable pageable) {
        List<Post> posts = queryFactory
                .select(post)
                .from(post)
                .leftJoin(post.account, account).fetchJoin()
                .leftJoin(post.tags, tag).fetchJoin()
                .leftJoin(post.comments, comment).fetchJoin()
                .orderBy(post.likeCount.desc())
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
}
