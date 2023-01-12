package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
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

    private class CreateTestMember {
        private Member saveMember;
        private SignUpDto signUpDto;

        public CreateTestMember() {
            SignUpDto signUpDto = new SignUpDto(
                    "test1",
                    "200000101",
                    "01012341234",
                    AuthType.DIRECT,
                    ""+(int) (Math.random()*100000),
                    "testPassword",
                    "testId"+(int) (Math.random()*100),
                    "부산컴퓨터과학고등학교",
                    "부산",
                    "인스타아이디",
                    "인스타비밀번호"
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
    public void 코드_없이_회원가입() {
        CreateTestMember member = new CreateTestMember();

        Optional<Member> findMember = memberRepository.findById(member.saveMember.getId());

        assertThat(findMember.get().getStudentNumber()).isEqualTo(member.signUpDto.getStudentNumber());
        assertThat(MemberType.PENDING).isEqualTo(findMember.get().getMemberType());
    }

    @Test
    public void 코드_있이_회원가입() {
        List<ReferCode> referCodeList = referCodeService.getReferCodeList();

        SignUpDto signUpDto = new SignUpDto(
                "test1",
                "200000101",
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

    @Test
    public void 중복코드_회원가입() {
        List<ReferCode> referCodeList = referCodeService.getReferCodeList();

        SignUpDto signUpDto = new SignUpDto(
                "test1",
                "200000101",
                "01012341234",
                AuthType.INDIRECT,
                "20786",
                "testPassword",
                "testId0921939",
                "부산컴퓨터과학고등학교",
                "부산",
                "인스타아이디",
                "인스타비밀번호",
                referCodeList.get(0).getCode()
        );
        memberService.saveMember(signUpDto);

        SignUpDto signUpDto2 = new SignUpDto(
                "test1",
                "200000101",
                "01012341234",
                AuthType.INDIRECT,
                "12323",
                "testPassword",
                "testId23212",
                "부산컴퓨터과학고등학교",
                "부산",
                "인스타아이디",
                "인스타비밀번호",
                referCodeList.get(0).getCode()
        );

        Assertions.assertThrows(IllegalStateException.class, () -> memberService.saveMember(signUpDto2));
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

    @Test
    public void 승인대기회원_승인_200() {
        CreateTestMember createTestMember = new CreateTestMember();

        Member member = createTestMember.saveMember;
        memberService.acceptPendingMember(new PendingMemberDto(
                member.getCreatedDate(),
                member.getBirthDay(),
                member.getName(),
                member.getStudentNumber()
        ));

        assertThat(member.getMemberType()).isEqualTo(MemberType.ACCEPT);
    }

    @Test
    public void 승인대기회원_승인_404() {
        CreateTestMember createTestMember = new CreateTestMember();

        Member member = createTestMember.saveMember;
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.acceptPendingMember(new PendingMemberDto(
                    member.getCreatedDate(),
                    member.getBirthDay(),
                    "",
                    member.getStudentNumber()
            ));
        });
    }
    // === 회원 승인, 거절 관련 ===
}