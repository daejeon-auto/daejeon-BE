package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.*;
import com.pcs.daejeon.entity.sanction.Report;
import com.pcs.daejeon.repository.custom.ReportRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory query;
    private final Util util;

    @Override
    public boolean validReport(Long postId) {
        Member member = util.getLoginMember();

        QMember reportedBy = new QMember("reportedBy");
        QPost reportedPost = new QPost("reportedPost");

        Report report = query
                .selectFrom(QReport.report)
                .innerJoin(QReport.report.reportedBy, reportedBy)
                .innerJoin(QReport.report.reportedPost, reportedPost)
                .where(reportedBy.id.eq(member.getId()), reportedPost.id.eq(postId))
                .fetchOne();

        // 해당 학교와 같은 학교인지 확인
        Post post = query
                .selectFrom(QPost.post)
                .where(QPost.post.id.eq(postId))
                .fetchOne();

        if (post == null) throw new IllegalStateException("not found post");
        if (!Objects.equals(util.getLoginMember().getSchool().getId(),
                post.getSchool().getId())) throw new IllegalStateException("school is different");

        return report != null;
    }
}
