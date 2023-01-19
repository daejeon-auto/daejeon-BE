package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.repository.MemberRepository;
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

    @Test
    public void 회원가입() {
        // TODO 학교 아이디로 식별
        SignUpDto signUpDto = new SignUpDto(
                "test",
                "010101",
                "01012341234",
                AuthType.DIRECT,
                "20202",
                "password",
                "test",
                "부산컴퓨터과학고등학교",
                "부산",
                "인스타아이디",
                "인스타비밀번호"
        );

        Member member = memberRepository.createMember(signUpDto);

        memberRepository.save(member);

        Member test = memberRepository.findByLoginId("test");

        assertThat(member.getId()).isEqualTo(test.getId());
    }
}