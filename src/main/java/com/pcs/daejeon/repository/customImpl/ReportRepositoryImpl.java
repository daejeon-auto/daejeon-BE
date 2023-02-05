package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.*;
import com.pcs.daejeon.repository.custom.ReportRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;

import static com.pcs.daejeon.entity.QReport.report;

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

        Report result = query
                .selectFrom(report)
                .innerJoin(report.reportedBy, reportedBy)
                .innerJoin(report.reportedPost, reportedPost)
                .where(reportedBy.id.eq(member.getId()), reportedPost.id.eq(postId))
                .fetchOne();

        // 해당 학교와 같은 학교인지 확인
        School school = query
                .selectFrom(QPost.post.school)
                .where(QPost.post.id.eq(postId))
                .fetchOne();

        return result == null &&
                Objects.equals(
                        util.getLoginMember().getSchool().getId(),
                        Objects.requireNonNull(school).getId());
    }
}
