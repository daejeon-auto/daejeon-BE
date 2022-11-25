package com.pcs.daejeon.service;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public void report(String reason, Long postId) {
        if (reportRepository.validReport()) {
            addReport(reason, postId);
            return;
        }

        removeReport(postId);
    }

    private void addReport(String reason, Long postId) {
        Member loginMember = memberRepository.getLoginMember();

        Report report = new Report(reason, loginMember);
        reportRepository.save(report);
    }

    private void removeReport(Long postId) {
        Member loginMember = memberRepository.getLoginMember();

        Report report = reportRepository.findByIdAndReportedByIs(postId, loginMember);
        if (report == null) {
            throw new IllegalStateException("not found report");
        }

        reportRepository.delete(report);
    }
}
