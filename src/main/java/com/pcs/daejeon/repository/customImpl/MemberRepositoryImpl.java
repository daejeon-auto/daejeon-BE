package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.member.Member;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.custom.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.pcs.daejeon.entity.QMember.*;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final PasswordEncoder pwdEncoder;
    private final JPAQueryFactory query;

    @Override
    public boolean validStudentNum(String stdNum) {
        Long result = query
                .select(member.count())
                .from(member)
                .where(
                        member.studentNumber.eq(stdNum),
                        member.memberType.ne(MemberType.GRADUATE)
                )
                .fetchOne();

        return result != 0;
    }

    @Override
    public boolean validLoginId(String loginId) {
        Long aLong = query
                .select(member.count())
                .from(member)
                .where(member.loginId.eq(loginId))
                .fetchOne();

        return aLong != 0;
    }

    public Member createMember(SignUpDto signUpDto) {
        return new Member(
                signUpDto.getName(),
                signUpDto.getBirthDay(),
                signUpDto.getPhoneNumber(),
                signUpDto.getStudentNumber(),
                pwdEncoder.encode(signUpDto.getPassword()),
                signUpDto.getLoginId(),
                signUpDto.getAuthType()
        );
    }
}
