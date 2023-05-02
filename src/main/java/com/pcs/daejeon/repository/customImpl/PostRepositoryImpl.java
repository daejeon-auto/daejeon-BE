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

        JPAQuery<Tuple> tupleJPAQuery = query
                .select(post, like, report)
                .distinct() // 1:N 관계에서 첫 인덱스가 중복으로 불리는 것을 해결(쿼리에 따라 작동 안할수도)
                .from(post)
                .where(post.postType.eq(PostType.SHOW),
                        post.school.id.eq(schoolId))
                .leftJoin(post.like, like) // 해당 포스트의 like를 like라는 이름으로 갖고옴
                    .on(like.likedBy.id.eq(loginMember == null ? 0L : loginMember.getId())) // 해당 유저가 한 좋아요 필터링
                .leftJoin(post.reports, report) // 해당 포스트의 report를 report라는 이름으로 갖고옴
                    .on(report.reportedBy.id.eq(loginMember == null ? 0L : loginMember.getId())); // 유저가 작성한 신고 필터링

        List<Tuple> result = tupleJPAQuery
                .orderBy(post.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> total = query
                .select(post.count())
                .from(post)
                .where(post.postType.eq(PostType.SHOW),
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
                        post.postType.eq(PostType.BLIND),
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
                .where(post.postType.eq(PostType.BLIND));

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
                .where(post.postType.eq(PostType.SHOW));

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

