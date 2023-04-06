package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@ActiveProfiles("test")
class MemberRepositoryImplTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    Util util;

    @Test
    public void 회원가입() {
        School school = new School("부산컴과고",
                "부산",
                "",
                "인스타아이디",
                "인스타비밀번호");

        School save = schoolRepository.save(school);

        SignUpDto signUpDto = new SignUpDto(
                "01012341234",
                AuthType.DIRECT,
                save.getId(),
                "password",
                "test"
        );

        Member member = util.createMember(signUpDto);

        memberRepository.save(member);

        Member test = memberRepository.findByLoginId("test");

        assertThat(member.getId()).isEqualTo(test.getId());
    }
}