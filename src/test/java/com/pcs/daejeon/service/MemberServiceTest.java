package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
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

    @Test
    @DisplayName("회원가입 성공")
    void signUp() throws MethodArgumentNotValidException {

        Member loginMember = util.getLoginMember();

        // given
        SignUpDto signUpDto = new SignUpDto(
                "01012341234",
                loginMember.getSchool().getId(),
                "testPassword",
                "signUpLoginId"
        );

        // when
        Member member = memberService.saveMember(signUpDto);

        // then
        assertThat(member.getLoginId()).isEqualTo(signUpDto.getLoginId());
        assertThat(member.getPassword()).isNotEqualTo(signUpDto.getPassword());
    }


    @Test
    @DisplayName("회원가입 실패 - 중복가입")
    void signUpFailDuplicateLoginId() throws MethodArgumentNotValidException {

        // given
        Long schooolId = util.getLoginMember().getSchool().getId();

        SignUpDto signUpDto = new SignUpDto(
                "01012341234",
                schooolId,
                "testPassword",
                "signUpLoginId"
        );
        memberService.saveMember(signUpDto);

        // when
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> memberService.saveMember(signUpDto));

        // then
        assertThat(exception.getMessage()).isEqualTo("student already sign up");
    }

    @Test
    @DisplayName("회원가입 실패 - 없는 학교")
    void signUpFailSchoolId() throws MethodArgumentNotValidException {

        // given
        SignUpDto signUpDto = new SignUpDto(
                "01012341234",
                99999L,
                "testPassword",
                "signUpLoginId"
        );

        // when
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> memberService.saveMember(signUpDto));

        // then
        assertThat(exception.getMessage()).isEqualTo("school not found");
    }
}