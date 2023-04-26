package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.ReportRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired
    ReportService reportService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    SchoolRepository schoolRepository;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    Util util;

    @Test
    @DisplayName("신고 성공")
    void addReport200() {
        School school = util.getLoginMember().getSchool();
        Post post = postRepository.save(new Post("hello world", school));

        reportService.report("reason", post.getId());
        List<Report> byId = reportRepository.findAllByReportedPostId(post.getId());
        assertFalse(byId.isEmpty());
    }

    @Test
    @DisplayName("신고 실패 - reason부족")
    void addReport400() {
        School school = util.getLoginMember().getSchool();
        Post post = postRepository.save(new Post("hello world", school));

        reportService.report("reason", post.getId());
    }

    @Test
    @DisplayName("신고 실패 - post 없음")
    void addReport404() {
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> reportService.report("reason-----", 0L),
                "not found post");
    }

    @Test
    @DisplayName("신고 5회로 인한 차단")
    @WithMockCustomUser
    void rejectPost() {
        School school = util.getLoginMember().getSchool();
        Post helloWorld = postRepository.save(new Post("hello world", school));
        Assertions.assertThat(helloWorld.getPostType()).isEqualTo(PostType.ACCEPTED);

        for (int i = 0; i < 4; i++) {
            Member testMember = new Member(
                    "01012341234",
                    "password" + i,
                    "loginId" + i,
                    school
            );
            memberRepository.save(testMember);
            reportRepository.save(new Report(
                    "reason",
                    testMember,
                    helloWorld
            ));
        }

        reportService.report("reason", helloWorld.getId());

        Assertions.assertThat(helloWorld.getPostType()).isEqualTo(PostType.REJECTED);
    }

    @Test
    void removeReport() {

    }
}