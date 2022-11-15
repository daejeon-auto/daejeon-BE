package com.pcs.daejeon.service;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.member.IndirectMember;
import com.pcs.daejeon.entity.member.Member;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReferCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReferCodeRepository referCodeRepository;


    public Member saveMember(SignUpDto signUpDto) {
        if (memberRepository.validStudentNum(signUpDto.getStudentNumber()) ||
                memberRepository.validLoginId(signUpDto.getLoginId())) {
            throw new IllegalStateException("student already sign up");
        }


        Member member = memberRepository.createMember(signUpDto); // password encode
        if (member.getAuthType() == AuthType.INDIRECT) {
            ReferCode referCode = referCodeRepository.findUnusedReferCode(signUpDto.getReferCode());
            referCode.useCode((IndirectMember) member);
            member.setMemberType(MemberType.ACCEPT);
        }

        return memberRepository.save(member);
    }

}
