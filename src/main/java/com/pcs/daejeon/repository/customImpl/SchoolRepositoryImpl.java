package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.custom.SchoolRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.pcs.daejeon.entity.QSchool.school;

@RequiredArgsConstructor
public class SchoolRepositoryImpl implements SchoolRepositoryCustom {

    final JPAQueryFactory query;

    @Override
    public boolean valiSchool(School schoolData) {
        Long result = query
                .select(school.count())
                .from(school)
                .where(
                        school.locate.eq(schoolData.getLocate()),
                        school.name.eq(schoolData.getName())
                )
                .fetchOne();

        return result != 0;
    }

    @Override
    public boolean validInstaId(String instaId) {
        Long result = query
                .select(school.count())
                .from(school)
                .where(
                        school.instaId.eq(instaId)
                )
                .fetchOne();

        return result != 0;
    }
}
