package com.pcs.daejeon.repository.customRepository;

import com.pcs.daejeon.entity.Post;
import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryCustom {

    QueryResults<Post> pagingPost(Pageable page);
}
