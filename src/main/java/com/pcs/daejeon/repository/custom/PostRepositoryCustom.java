package com.pcs.daejeon.repository.custom;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {

    QueryResults<Tuple> pagingPost(Pageable page);
}
