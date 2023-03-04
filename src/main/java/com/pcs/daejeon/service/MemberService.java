package com.pcs.daejeon.service;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReferCodeRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReferCodeRepository referCodeRepository;
    private final SchoolRepository schoolRepository;
    private final Util util;


    public Member saveMember(SignUpDto signUpDto) throws MethodArgumentNotValidException {
        if (memberRepository.validStudentNum(signUpDto.getStudentNumber()) ||
                memberRepository.validLoginId(signUpDto.getLoginId())) {
            throw new IllegalStateException("student already sign up");
        }

        Member member = util.createMember(signUpDto); // password encode
        if (member.getAuthType() == AuthType.INDIRECT) {
            ReferCode referCode = referCodeRepository.findUnusedReferCode(signUpDto.getReferCode());
            if (referCode == null) {
                throw new IllegalStateException("unused refer code not found");
            }
            if (!member.getSchool().getId().equals(
                    referCode.getCreatedBy().getSchool().getId())) {
                throw new IllegalStateException("school is different");
            }

            member.setMemberType(MemberType.ACCEPT);
            member.useCode(referCode);
        }

        return memberRepository.save(member);
    }

    public Member saveAdmin(SignUpDto signUpDto) throws MethodArgumentNotValidException {
        Member member = saveMember(signUpDto);

        member.setMemberType(MemberType.ACCEPT);
        member.setRole(RoleTier.ROLE_TIER2);

        return member;
    }

    public void acceptMember(Long memberId) {
        Optional<Member> acceptMember = memberRepository.findById(memberId);
        if (acceptMember.isEmpty()) {
            throw new IllegalStateException("member not found");
        }

        if (isNotSameSchool(acceptMember.get())) {
            throw new IllegalStateException("school is different");
        }

        Member member = acceptMember.get();
        member.setMemberType(MemberType.ACCEPT);

        log.info("[accept-member] accept member: id["+ member.getId() +"]"+ util.getLoginMember().getId());
    }

    public void rejectMember(Long memberId) {
        Optional<Member> byId = memberRepository.findById(memberId);
        if (byId.isEmpty()) {
            throw new IllegalStateException("member not found");
        }
        Member member = byId.get();

        if (isNotSameSchool(member)) {
            throw new IllegalStateException("school is different");
        }

        member.setMemberType(MemberType.REJECT);
        log.info("[reject-member] reject member: id["+ member.getId() +"]"+ util.getLoginMember().getId());
    }

    public List<Member> getMembers(Long memberId, boolean onlyAdmin) {
        return memberRepository.getMemberList(memberId, onlyAdmin, util.getLoginMember().getSchool());
    }

    public List<Member> getPendingMembers() {
        return memberRepository.findAllByMemberTypeAndSchoolOrderByCreatedDateAsc(MemberType.PENDING, util.getLoginMember().getSchool());
    }

    public void acceptPendingMember(@Valid PendingMemberDto pendingMemberDto) {
        Optional<School> school = getSchool(pendingMemberDto);
        Member member = memberRepository.findByNameAndBirthDayAndStudentNumberAndSchool(
                pendingMemberDto.getName(),
                pendingMemberDto.getBirthday(),
                pendingMemberDto.getStd_number(),
                school.get()
        );

        if (member == null) {
            throw new IllegalStateException("not found member");
        }

        member.setMemberType(MemberType.ACCEPT);
    }

    @NotNull
    private Optional<School> getSchool(PendingMemberDto pendingMemberDto) {
        Optional<School> school = schoolRepository.findById(pendingMemberDto.getSchoolId());

        if (school.isEmpty()) throw new IllegalArgumentException("not found school");
        return school;
    }

    public void rejectPendingMember(PendingMemberDto pendingMemberDto) {
        Optional<School> school = getSchool(pendingMemberDto);

        Member member = memberRepository.findByNameAndBirthDayAndStudentNumberAndSchool(
                pendingMemberDto.getName(),
                pendingMemberDto.getBirthday(),
                pendingMemberDto.getStd_number(),
                school.get()
        );

        if (member == null) {
            throw new IllegalStateException("not found member");
        }

        referCodeRepository.deleteAll(member.getReferCodes());
        memberRepository.delete(member);
    }

    public Member findPersonalInfo(Long memberId) {
        Optional<Member> byId = memberRepository.findByIdAndSchool(memberId, util.getLoginMember().getSchool());

        if (byId.isEmpty()) {
            throw new IllegalStateException("not found member");
        }

        return byId.get();
    }

    public Member setMemberRole(Long memberId, RoleTier tier) {
        Optional<Member> member = memberRepository.findByIdAndSchool(memberId, util.getLoginMember().getSchool());

        if (member.isEmpty()) {
            throw new IllegalStateException("not found member");
        }

        member.get().setRole(tier);

        return member.get();
    }

    private boolean isNotSameSchool(Member acceptMember) {
        Member admin = util.getLoginMember();
        return admin.getSchool() != acceptMember.getSchool();
    }
}
