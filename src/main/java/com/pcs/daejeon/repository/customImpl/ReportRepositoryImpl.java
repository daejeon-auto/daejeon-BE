package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.QReport;
import com.pcs.daejeon.entity.Report;
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

        Report result = jpaQueryFactory
                .selectFrom(report)
                .where(report.reportedBy.id.eq(member.getId()), report.reportedPost.id.eq(postId))
                .fetchOne();
        return result == null;
    }
}
