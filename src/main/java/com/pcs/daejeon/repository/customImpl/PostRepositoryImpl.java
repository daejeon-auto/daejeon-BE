package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.common.Util;
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

import java.util.List;

import static com.pcs.daejeon.entity.QLike.like;
import static com.pcs.daejeon.entity.QPost.post;
import static com.pcs.daejeon.entity.QReport.report;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;
    private final Util util;

    @Override
    public Page<Tuple> pagingPost(Pageable page, Long schoolId) {

        Member loginMember = util.getLoginMember();

        QPost likePost = new QPost("likePost");
        QPost reportedPost = new QPost("reportedPost");
        QMember likedBy = new QMember("likedBy");
        QMember reportedBy = new QMember("reportedBy");

        JPAQuery<Tuple> tupleJPAQuery = query
                .select(post, likedBy, reportedBy)
                .from(post)
                .leftJoin(post.like, like) // 해당 포스트의 like를 like라는 이름으로 갖고옴
                .leftJoin(like.likedBy, likedBy) // 좋아요 박은 유저 ID
                    .on(likedBy.id.eq(loginMember == null ? 0L : loginMember.getId())) // 로그인 유저가 아니면 FALSE
                .leftJoin(post.reports, report)
                .leftJoin(report.reportedBy, reportedBy)
                    .on(reportedBy.id.eq(loginMember == null ? 0L : loginMember.getId())); // 로그인 유저가 아니면 FALSE

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
                .where(post.postType.eq(PostType.ACCEPTED),
                        post.school.id.eq(schoolId));

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

