package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {

    QueryResults<Tuple> pagingPost(Pageable page);

    Long getLikedCount(Post post);
}
