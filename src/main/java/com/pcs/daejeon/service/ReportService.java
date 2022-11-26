package com.pcs.daejeon.service;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public void report(String reason, Long postId) {
        if (reportRepository.validReport()) {
            addReport(reason, postId);
            return;
        }

        removeReport(postId);
    }

    private void addReport(String reason, Long postId) {
        Member loginMember = memberRepository.getLoginMember();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new IllegalStateException("not found post");
        }

        Report report = new Report(reason, loginMember, post.get());
        reportRepository.save(report);
    }

    private void removeReport(Long postId) {
        Member loginMember = memberRepository.getLoginMember();

        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new IllegalStateException("not found post");
        }

        Report report = reportRepository.findByReportedPostAndReportedBy(post.get(), loginMember);
        if (report == null) {
            throw new IllegalStateException("not found report");
        }

        reportRepository.delete(report);
    }
}
