package com.pcs.daejeon.service;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* TODO: is login
      TODO: Login
      TODO: reset Password
     */

    public Member saveMember(SignUpDto signUpDto) {
        if (memberRepository.validStudentNum(signUpDto.getStudentNumber()) ||
                memberRepository.validLoginId(signUpDto.getLoginId())) {
            throw new IllegalStateException("student already sign up");
        }


        Member member = memberRepository.createMember(signUpDto); // password encode
        System.out.println(member.getPassword());
        return memberRepository.save(member);
    }

}
