package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.type.PostType;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {

    QueryResults<Tuple> pagingPost(Pageable page);
    QueryResults<Post> pagingRejectPost(Pageable page, Long memberId, Long reportCount);

    Long getLikedCount(Post post);
}
