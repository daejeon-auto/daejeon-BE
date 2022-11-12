package com.pcs.daejeon.service;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /* TODO: is login
      TODO: Login
      TODO: reset Password
     */

    public Member saveMember(Member member) {
        if (memberRepository.validStudentNum(member.getStudentNumber())) {
            throw new IllegalStateException("student already sign up");
        }

        return memberRepository.save(member);
    }

}
