package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.ReportRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@ActiveProfiles("test")
class ReportRepositoryImplTest {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 신고_가능() {
        Post post = new Post("test글 작성");

        Post save = postRepository.save(post);

        boolean valid = reportRepository.validReport(save.getId());

        assertThat(valid).isTrue();
    }

    @Test
    public void 신고_불가능() {
        Post post = new Post("test글 작성");
        Post save = postRepository.save(post);

        Report report = new Report("test", memberRepository.getLoginMember(), post);
        reportRepository.save(report);

        boolean valid = reportRepository.validReport(save.getId());

        assertThat(valid).isFalse();
    }
}