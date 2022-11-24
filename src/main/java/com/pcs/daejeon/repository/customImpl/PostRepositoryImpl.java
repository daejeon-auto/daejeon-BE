package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.custom.PostRepositoryCustom;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static com.pcs.daejeon.entity.QLike.like;
import static com.pcs.daejeon.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public QueryResults<Tuple> pagingPost(Pageable page) {
        PrincipalDetails member = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()!= null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {

            member = (PrincipalDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
        }

        JPAQuery<Tuple> tupleJPAQuery = query
                .select(post, like)
                .from(post)
                .where(post.postType.eq(PostType.ACCEPTED))
                .leftJoin(post.like, like);
        if (member != null) {
            tupleJPAQuery
                .on(like.post.id.eq(post.id), like.likedBy.id.eq(member.getMember().getId()));
        } else {
            tupleJPAQuery
                    .on(like.post.id.eq(-1L));
        }

        QueryResults<Tuple> result = tupleJPAQuery
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(20)
                .fetchResults();

        return result;
    }

    @Override
    public Long getLikedCount(Post post) {
        return query.select(like.count())
                .from(like)
                .where(like.post.eq(post))
                .fetchOne();
    }
}
