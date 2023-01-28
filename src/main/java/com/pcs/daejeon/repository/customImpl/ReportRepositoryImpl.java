package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.QMember;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.custom.ReportRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.pcs.daejeon.entity.QReport.report;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final Util util;

    @Override
    public boolean validReport(Long postId) {
        Member member = util.getLoginMember();

        QMember reportedBy = new QMember("reportedBy");
        QPost reportedPost = new QPost("reportedPost");

        Report result = jpaQueryFactory
                .selectFrom(report)
                .innerJoin(report.reportedBy, reportedBy)
                .innerJoin(report.reportedPost, reportedPost)
                .where(reportedBy.id.eq(member.getId()), reportedPost.id.eq(postId))
                .fetchOne();
        return result == null;
    }
}
