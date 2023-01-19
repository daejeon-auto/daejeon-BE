package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.ReportRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    @DisplayName("신고 성공")
    void addReport200() {
        School school = schoolRepository.save(new School(
                "부산컴과고",
                "부산",
                "인스타아이디",
                "인스타패스워드"
        ));
        Post post = postRepository.save(new Post("hello world", school));

        reportService.report("reason", post.getId());
        Optional<Report> byId = reportRepository.findById(post.getId());
        assertFalse(byId.isEmpty());
    }

    @Test
    @DisplayName("신고 실패 - reason부족")
    void addReport400() {
        School school = schoolRepository.save(new School(
                "부산컴과고",
                "부산",
                "인스타아이디",
                "인스타패스워드"
        ));
        Post post = postRepository.save(new Post("hello world", school));

        reportService.report("reason", post.getId());
    }

    @Test
    void removeReport() {

    }
}