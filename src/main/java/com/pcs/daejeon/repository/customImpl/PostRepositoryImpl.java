package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.custom.PostRepositoryCustom;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static com.pcs.daejeon.entity.QLike.like;
import static com.pcs.daejeon.entity.QPost.post;
import static com.pcs.daejeon.entity.QReport.report;

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
                .select(post, like, report)
                .from(post)
                .leftJoin(post.like, like);
        if (member != null) {
            tupleJPAQuery
                .on(like.post.id.eq(post.id), like.likedBy.id.eq(member.getMember().getId()))
                .leftJoin(post.reports, report)
                .on(report.reportedPost.id.eq(post.id), report.reportedBy.id.eq(member.getMember().getId()));
        } else {
            tupleJPAQuery
                .on(like.post.id.eq(0L))
                .leftJoin(post.reports, report)
                .on(report.reportedPost.id.eq(0L));
        }

        QueryResults<Tuple> result = tupleJPAQuery
                .where(post.postType.eq(PostType.ACCEPTED))
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(20)
                .fetchResults();

        return result;
    }

    @Override
    public QueryResults<Post> pagingRejectPost(Pageable page, Long memberId, Long reportCount) {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

        BooleanExpression codState = post.postType.eq(PostType.REJECTED);

        if (memberId != null) {
            codState = codState.and(post.createdBy.eq(String.valueOf(memberId)));
        }
        if (reportCount != null) {
            codState = codState.and(post.reports.size().eq(Math.toIntExact(reportCount)));
        }

        QueryResults<Post> result = query
                .selectFrom(post)
                .where(codState)
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

