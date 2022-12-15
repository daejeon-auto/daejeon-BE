package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    boolean validStudentNum(String stdNum);
    boolean validLoginId(String loginId);

    Member getLoginMember();

    Member createMember(SignUpDto signUpDto);

    List<Member> getMemberList(Long memberId, boolean onlyAdmin);
}
