package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReferCodeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;

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

        public Member getSaveMember() {
            return saveMember;
        }

        public SignUpDto getSignUpDto() {
            return signUpDto;
        }

        public CreateTestMember() {
            SignUpDto signUpDto = new SignUpDto(
                    "test1",
                    "20050323",
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
                Member saveMember = memberService.saveMember(signUpDto);
                this.saveMember = saveMember;
                this.signUpDto = signUpDto;
                return;
            } catch(Exception e) {
                // 만일 같은 값을 가져 already signed up 에러가 뜨면 새로 랜덤값을 뽑음
                CreateTestMember member = new CreateTestMember();
                this.saveMember = member.saveMember;
                this.signUpDto = member.signUpDto;
                return;
            }
        }
    }




    // === 추천 코드 회원가입 ===
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
    // === 추천 코드 회원가입 ===


    // === 회원 승인, 거절 관련 ===
    @Test
    public void 회원_승인_200() {
        CreateTestMember member = new CreateTestMember();

        memberService.acceptMember(member.saveMember.getId());

        assertThat(member.saveMember.getMemberType()).isEqualTo(MemberType.ACCEPT);
    }

    @Test
    public void 회원_승인_404() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.acceptMember(100L);
        });
    }

    @Test
    public void 회원_거절_200() {
        CreateTestMember member = new CreateTestMember();

        memberService.rejectMember(member.saveMember.getId());

        assertThat(member.saveMember.getMemberType()).isEqualTo(MemberType.REJECT);
    }

    @Test
    public void 회원_거절_404() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.acceptMember(100L);
        });
    }
    // === 회원 승인, 거절 관련 ===
}