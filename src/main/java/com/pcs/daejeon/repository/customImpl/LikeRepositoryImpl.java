package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.QMember;
import com.pcs.daejeon.repository.custom.LikeRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.pcs.daejeon.entity.QLike.*;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public boolean validLike(Member member, Long postId) {
        Long aLong = query
                .select(like.count())
                .from(like)
                .where(like.likedBy.eq(member), like.post.id.eq(postId))
                .fetchOne();

        Integer schoolValid = query
                .selectOne()
                .from(QMember.member)
                .where(
                        QMember.member.school.id.eq(like.post.school.id)
                )
                .fetchOne();
        return aLong != 0 && schoolValid != 0;
    }
}
