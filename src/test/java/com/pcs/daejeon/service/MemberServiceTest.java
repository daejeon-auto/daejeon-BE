package com.pcs.daejeon.service;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void signUpTest() {
        SignUpDto signUpDto = new SignUpDto(
                "test1",
                "20050323",
                "01027729778",
                AuthType.DIRECT,
                "20201",
                "koldin13579",
                "koldin"
        );

        Member saveMember = memberService.saveMember(signUpDto);
        Optional<Member> findMember = memberRepository.findById(saveMember.getId());

        assertThat(findMember.get().getStudentNumber()).isEqualTo(signUpDto.getStudentNumber());
    }
}