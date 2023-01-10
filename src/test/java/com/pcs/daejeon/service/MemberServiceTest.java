package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReferCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ReferCodeService referCodeService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void 코드_없이_회원가입() {
        SignUpDto signUpDto = new SignUpDto(
                "test1",
                "20050323",
                "01012341234",
                AuthType.DIRECT,
                "20203",
                "testPassword",
                "testId3",
                "부산컴퓨터과학고등학교",
                "부산",
                "인스타아이디",
                "인스타비밀번호"
        );

        Member saveMember = memberService.saveMember(signUpDto);
        Optional<Member> findMember = memberRepository.findById(saveMember.getId());

        assertThat(findMember.get().getStudentNumber()).isEqualTo(signUpDto.getStudentNumber());
        assertThat(MemberType.PENDING).isEqualTo(findMember.get().getMemberType());
    }

    @Test
    public void 코드_있이_회원가입() {
        List<ReferCode> referCodeList = referCodeService.getReferCodeList();

        SignUpDto signUpDto = new SignUpDto(
                "test1",
                "20050323",
                "01012341234",
                AuthType.INDIRECT,
                "20203",
                "testPassword",
                "testId3",
                "부산컴퓨터과학고등학교",
                "부산",
                "인스타아이디",
                "인스타비밀번호",
                referCodeList.get(0).getCode()
        );

        Member saveMember = memberService.saveMember(signUpDto);
        Optional<Member> findMember = memberRepository.findById(saveMember.getId());

        assertThat(findMember.get().getStudentNumber()).isEqualTo(signUpDto.getStudentNumber());
        assertThat(MemberType.ACCEPT).isEqualTo(findMember.get().getMemberType());
    }
}