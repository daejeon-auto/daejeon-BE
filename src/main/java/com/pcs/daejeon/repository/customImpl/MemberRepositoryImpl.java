package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.custom.MemberRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.pcs.daejeon.entity.QMember.member;


@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory query;

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
    public boolean validPhoneNumber(String phoneNumber) {
        Long aLong = query
                .select(member.count())
                .from(member)
                .where(member.phoneNumber.eq(phoneNumber))
                .fetchOne();

        return aLong != 0;
    }

    @Override
    public List<Member> getMemberList(Long memberId, boolean onlyAdmin, School school) {
        BooleanExpression codState = member.memberType.ne(MemberType.GRADUATE)
                .and(member.school.id.eq(school.getId()));
        if (memberId != null) {
            codState = codState.and(member.id.eq(memberId));
        }
        if (onlyAdmin) {
            codState = codState
                        .and(member.role.eq(RoleTier.ROLE_TIER1));
        }

        return query
                .selectFrom(member)
                .where(codState)
                .orderBy(member.id.desc())
                .fetch();
    }
}
