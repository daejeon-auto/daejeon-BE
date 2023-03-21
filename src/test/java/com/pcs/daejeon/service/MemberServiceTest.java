package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
@WithMockCustomUser
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SchoolRepository schoolRepository;
    @Autowired
    Util util;
    @Autowired
    EntityManager em;


    private class CreateTestMember {
        private Member saveMember;
        private SignUpDto signUpDto;

        public CreateTestMember() {

            SignUpDto signUpDto = new SignUpDto(
                    "01012341234",
                    AuthType.DIRECT,
                    util.getLoginMember().getSchool().getId(),
                    "testPassword",
                    "testId"+(int) (Math.random()*100)
            );

            try {
                this.saveMember = memberService.saveMember(signUpDto);
                this.signUpDto = signUpDto;
            } catch(Exception e) {
                // 만일 같은 값을 가져 already signed up 에러가 뜨면 새로 랜덤값을 뽑음
                CreateTestMember member = new CreateTestMember();
                this.saveMember = member.saveMember;
                this.signUpDto = member.signUpDto;
            }
        }
    }

    // === 회원가입 ===
    @Test
    public void 회원가입_실패_존재하는_계정() {
        CreateTestMember createTestMember = new CreateTestMember();

        Assertions.assertThrows(IllegalStateException.class, () -> memberService.saveMember(createTestMember.signUpDto));
    }

    @Test
    public void 회원가입() throws MethodArgumentNotValidException {
        SignUpDto signUpDto = new SignUpDto(
                "01012341234",
                AuthType.INDIRECT,
                util.getLoginMember().getSchool().getId(),
                "testPassword",
                "testId3"
        );

        Member saveMember = memberService.saveMember(signUpDto);
        Optional<Member> findMember = memberRepository.findById(saveMember.getId());

        assertThat(findMember.get().getLoginId()).isEqualTo(signUpDto.getLoginId());
        assertThat(MemberType.ACCEPT).isEqualTo(findMember.get().getMemberType());
    }

    // === 회원가입 ===


    // === 회원 승인, 거절 관련 ===
    @Test
    public void 회원_승인_200() {
        CreateTestMember member = new CreateTestMember();

        memberService.acceptMember(member.saveMember.getId());

        assertThat(member.saveMember.getMemberType()).isEqualTo(MemberType.ACCEPT);
    }

    @Test
    public void 회원_승인_404() {
        Assertions.assertThrows(IllegalStateException.class, () -> memberService.acceptMember(0L));
    }

    @Test
    public void 회원_거절_200() {
        CreateTestMember member = new CreateTestMember();

        memberService.rejectMember(member.saveMember.getId());

        assertThat(member.saveMember.getMemberType()).isEqualTo(MemberType.REJECT);
    }

    @Test
    public void 회원_거절_404() {
        Assertions.assertThrows(IllegalStateException.class, () -> memberService.acceptMember(100L));
    }


    // === 회원 권한 수정 ===
    @Test
    public void 회원_권한_수정_200() {
        CreateTestMember createTestMember = new CreateTestMember();
        memberService.setMemberRole(createTestMember.saveMember.getId(), RoleTier.ROLE_TIER2);

        assertThat(createTestMember.saveMember.getRole()).isEqualTo(RoleTier.ROLE_TIER2);
    }

    @Test
    public void 회원_권한_수정_404() {
        Assertions.assertThrows(IllegalStateException.class, () -> memberService.setMemberRole(0L, RoleTier.ROLE_TIER2));
    }
    // === 회원 권한 수정 ===
}