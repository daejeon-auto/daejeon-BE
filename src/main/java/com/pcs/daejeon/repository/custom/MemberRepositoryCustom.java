package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.member.Member;

public interface MemberRepositoryCustom {

    boolean validStudentNum(String stdNum);
    boolean validLoginId(String loginId);

    Member createMember(SignUpDto signUpDto);
}
