package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.QMember;
import com.pcs.daejeon.entity.QSchool;
import com.pcs.daejeon.repository.custom.LikeRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.pcs.daejeon.entity.QLike.*;
import static com.pcs.daejeon.entity.QPost.post;
import static com.pcs.daejeon.entity.QSchool.school;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public boolean validLike(Member member, Long postId) {
        Integer alreadyValid = query
                .selectOne()
                .from(like)
                .innerJoin(like.likedBy, QMember.member)
                .innerJoin(like.post, post)
                .where(QMember.member.eq(member), post.id.eq(postId))
                .fetchOne();

        QSchool memberSchool = new QSchool("memberSchool");
        QSchool postSchool = new QSchool("postSchool");
        Integer schoolValid = query
                .selectOne()
                .from(QMember.member, like)
                .innerJoin(QMember.member.school, memberSchool)
                .innerJoin(like.post, post)
                .innerJoin(post.school, postSchool)
                .where(memberSchool.id.eq(postSchool.id))
                .fetchOne();
        return alreadyValid != null && schoolValid != null;
    }
}
