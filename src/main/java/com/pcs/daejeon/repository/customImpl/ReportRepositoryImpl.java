package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.entity.*;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReportRepository;
import com.pcs.daejeon.repository.custom.ReportRepositoryCustom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pcs.daejeon.entity.QReport.*;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final MemberRepository memberRepository;

    @Override
    public boolean validReport(Long postId) {
        Member member = memberRepository.getLoginMember();

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
