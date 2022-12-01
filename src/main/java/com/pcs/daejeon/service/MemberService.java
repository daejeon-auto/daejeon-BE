package com.pcs.daejeon.service;

import com.pcs.daejeon.dto.account.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReferCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
            if (referCode == null) {
                throw new IllegalStateException("unused refer code not found");
            }
            referCode.useCode(member);
            member.setMemberType(MemberType.ACCEPT);
        }

        return memberRepository.save(member);
    }

    public void acceptMember(Long memberId) {
        Optional<Member> byId = memberRepository.findById(memberId);
        if (byId.isEmpty()) {
            throw new IllegalStateException("member not found");
        }
        Member member = byId.get();
        member.setMemberType(MemberType.ACCEPT);

        log.info("[accept-member] accept member: id["+ member.getId() +"]"+ memberRepository.getLoginMember().getId());
    }

    public void rejectMember(Long memberId) {
        Optional<Member> byId = memberRepository.findById(memberId);
        if (byId.isEmpty()) {
            throw new IllegalStateException("member not found");
        }
        Member member = byId.get();

        member.setMemberType(MemberType.REJECT);
        log.info("[reject-member] reject member: id["+ member.getId() +"]"+ memberRepository.getLoginMember().getId());
    }

    public List<Member> getMembers(Long memberId) {
        return memberRepository.getMemberList(memberId);
    }

    public List<Member> getPendingMembers() {
        return memberRepository.findByMemberTypePendingOrderByCreatedDateAsc();
    }
}
