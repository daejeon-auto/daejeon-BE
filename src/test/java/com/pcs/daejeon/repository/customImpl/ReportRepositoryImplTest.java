package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    Util util;

    @Test
    public void 신고_가능() {
        Member loginMember = util.getLoginMember();
        Post post = new Post("test글 작성", loginMember.getSchool());

        Post save = postRepository.save(post);

        boolean valid = reportRepository.validReport(save.getId());

        assertThat(valid).isTrue();
    }

    @Test
    public void 신고_불가능() {
        Member member = util.getLoginMember();
        Post post = new Post("test글 작성", member.getSchool());
        Post save = postRepository.save(post);

        Report report = new Report("test", util.getLoginMember(), post);
        reportRepository.save(report);

        boolean valid = reportRepository.validReport(save.getId());

        assertThat(valid).isFalse();
    }
}