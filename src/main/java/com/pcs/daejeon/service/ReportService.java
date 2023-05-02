package com.pcs.daejeon.service;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.sanction.Report;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.entity.type.ReportType;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.sanction.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final Util util;

    public void report(String reason, Long postId, ReportType reportType) {
        if (reportRepository.validReport(postId)) {
            return;
        }
        Member loginMember = util.getLoginMember();
        Optional<Post> post = postRepository.findById(postId);

        if (post.isEmpty()) throw new IllegalStateException("post not found");

        Report report = new Report(reason, loginMember, post.get(), reportType);
        reportRepository.save(report);

        Long reportCount = reportRepository.countByReportedPost(post.get());
        if (reportCount == 5) {
            post.get().setPostType(PostType.BLIND);
        }
        log.info("[add-report] report post: id["+ post.get().getId() +"] by - "+ loginMember.getId()+"["+ loginMember.getId()+"] reason: " + reason);
    }

    public List<Report> getReportList(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new IllegalArgumentException("not found post");
        }

        return reportRepository.findAllByReportedPostId(postId);
    }
}
