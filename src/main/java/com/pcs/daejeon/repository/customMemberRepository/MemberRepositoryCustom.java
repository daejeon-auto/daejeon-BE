package com.pcs.daejeon.repository.customMemberRepository;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;

public interface MemberRepositoryCustom {

    boolean validStudentNum(String stdNum);
    boolean validLoginId(String loginId);

    Member createMember(SignUpDto signUpDto);
}
