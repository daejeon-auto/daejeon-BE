package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.*;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.custom.PostRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

import static com.pcs.daejeon.entity.QLike.like;
import static com.pcs.daejeon.entity.QPost.post;
import static com.pcs.daejeon.entity.QReport.report;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Page<Tuple> pagingPost(Pageable page, Long schoolId) {
        PrincipalDetails member = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()!= null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {

            member = (PrincipalDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
        }

        QPost likePost = new QPost("likePost");
        QPost reportedPost = new QPost("reportedPost");
        QMember likedBy = new QMember("likedBy");
        QMember reportedBy = new QMember("reportedBy");

        JPAQuery<Tuple> tupleJPAQuery = query
                .select(post, like, report)
                .from(post)
                .leftJoin(post.like, like);

        tupleJPAQuery
                .leftJoin(like.post, likePost)
                .leftJoin(like.likedBy, likedBy)
                .on(likePost.id.eq(post.id), likedBy.id.eq(member == null ? 0L : member.getMember().getId()))
                .leftJoin(post.reports, report)
                .leftJoin(report.reportedPost, reportedPost)
                .leftJoin(report.reportedBy, reportedBy)
                .on(reportedPost.id.eq(post.id), reportedBy.id.eq(member == null ? 0L : member.getMember().getId()));

        List<Tuple> result = tupleJPAQuery
                .where(post.postType.eq(PostType.ACCEPTED),
                        post.school.id.eq(schoolId))
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> total = query
                .select(post.count())
                .from(post)
                .where(post.postType.eq(PostType.ACCEPTED));

        return PageableExecutionUtils.getPage(result, page, total::fetchOne);
    }

    @Override
    public Page<Post> pagingPostByMemberId(Pageable page, Member member) {

        List<Post> content = query
                .selectFrom(post)
                .where(post.createdBy.eq(member.getId().toString()))
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> total = query
                .select(post.count())
                .from(post)
                .where(post.createByMember.id.eq(member.getId()));

        return PageableExecutionUtils.getPage(content, page, total::fetchOne);
    }

    @Override
    public Page<Post> pagingRejectPost(Pageable page, Long memberId, Long reportCount) {
        List<Post> result = query
                .selectFrom(post)
                .where(
                        post.postType.eq(PostType.REJECTED),
                        memberIdEq(memberId),
                        reportCountEq(reportCount)
                )
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> total = query
                .select(post.count())
                .from(post)
                .where(post.postType.eq(PostType.REJECTED));

        return PageableExecutionUtils.getPage(result, page, total::fetchOne);
    }
    @Override
    public Page<Post> searchPost(Pageable page, Long memberId, Long reportCount, School school) {
        List<Post> result = query
                .selectFrom(post)
                .where(
                        memberIdEq(memberId),
                        reportCountEq(reportCount),
                        post.school.id.eq(school.getId())
                )
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> total = query
                .select(post.count())
                .from(post)
                .where(post.postType.eq(PostType.ACCEPTED), post.postType.eq(PostType.ACCEPTED));

        return PageableExecutionUtils.getPage(result, page, total::fetchOne);
    }

    @Override
    public Long getLikedCount(Post post) {
        return query.select(like.count())
                .from(like)
                .where(like.post.eq(post))
                .fetchOne();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? post.createdBy.eq(String.valueOf(memberId)) : null;
    }
    private BooleanExpression reportCountEq(Long reportCount) {
        return reportCount != null ? post.reports.size().eq(Math.toIntExact(reportCount)) : null;
    }
}

