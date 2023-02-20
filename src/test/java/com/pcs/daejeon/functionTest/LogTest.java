package com.pcs.daejeon.functionTest;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Log;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.repository.LogRepository;
import com.pcs.daejeon.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@WithMockCustomUser
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class LogTest {
    @Autowired
    MemberService memberService;

    @Autowired
    LogRepository logRepository;

    @Autowired
    Util util;

    @Test
    void logTest() throws MethodArgumentNotValidException {

        Member member = memberService.saveMember(
                new SignUpDto(
                        "logTest",
                        "234234",
                        "01012341234",
                        AuthType.DIRECT,
                        "00000",
                        util.getLoginMember().getSchool().getId(),
                        "password",
                        "loginId12312"
                )
        );

        memberService.acceptMember(member.getId());

        List<Log> school = logRepository.findAllBySchool(member.getSchool());

        Assertions.assertThat(school.isEmpty()).isEqualTo(false);
    }
}
