package com.pcs.daejeon.repository.customMemberRepository;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface MemberRepositoryCustom {

    boolean validStudentNum(String stdNum);

    Member createMember(SignUpDto signUpDto, PasswordEncoder pwdEncoder);
}
