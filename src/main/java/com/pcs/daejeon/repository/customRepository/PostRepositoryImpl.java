package com.pcs.daejeon.repository.customRepository;

import com.pcs.daejeon.entity.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;


import static com.pcs.daejeon.entity.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public QueryResults<Post> pagingPost(Pageable page) {

        return query
                .selectFrom(post)
                .orderBy(post.createdDate.desc())
                .offset(page.getOffset())
                .limit(20)
                .fetchResults();
    }
}
