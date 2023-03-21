package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;

import java.util.List;

public interface MemberRepositoryCustom {

    boolean validLoginId(String loginId);
    boolean validPhoneNumber(String phoneNumber);

    List<Member> getMemberList(Long memberId, boolean onlyAdmin, School school);
}
