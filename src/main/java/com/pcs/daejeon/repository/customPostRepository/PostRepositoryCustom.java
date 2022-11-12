package com.pcs.daejeon.repository.customPostRepository;

import com.pcs.daejeon.entity.Post;
import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {

    QueryResults<Post> pagingPost(Pageable page);
}
