package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.custom.PostRepositoryCustom;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;


import static com.pcs.daejeon.entity.QLike.*;
import static com.pcs.daejeon.entity.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public QueryResults<Tuple> pagingPost(Pageable page) {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        QueryResults<Tuple> result = query
                .select(post, like)
                .from(post)
                .where(post.postType.eq(PostType.ACCEPTED))
                .orderBy(post.id.desc())
                .leftJoin(post.like, like)
                .on(like.post.id.eq(post.id), like.likedBy.id.eq(member.getMember().getId()))
                .offset(page.getOffset())
                .limit(20)
                .fetchResults();
        return result;
    }
}
