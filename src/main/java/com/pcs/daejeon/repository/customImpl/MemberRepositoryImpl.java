package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.custom.MemberRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.pcs.daejeon.entity.QMember.member;


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

    @Override
    public List<Member> getMemberList(Long memberId) {
        BooleanExpression codState = member.memberType.ne(MemberType.GRADUATE).and(member.memberType.ne(MemberType.PENDING));
        if (memberId != null) {
            codState = codState.and(member.id.eq(memberId));
        }

        return query
                .selectFrom(member)
                .where(codState)
                .orderBy(member.id.desc())
                .fetch();
    }

    // TODO: PENDING 유저만 주는 메소드 만들기

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

    public Member getLoginMember() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return member.getMember();
    }

}
