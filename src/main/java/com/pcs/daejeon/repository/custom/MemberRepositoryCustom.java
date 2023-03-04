package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    boolean validStudentNum(String stdNum);
    boolean validLoginId(String loginId);

    List<Member> getMemberList(Long memberId, boolean onlyAdmin);
}
