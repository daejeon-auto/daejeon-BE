package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.School;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {

    Page<Tuple> pagingPost(Pageable page);
    Page<Post> pagingPostByMemberId(Pageable page, Member member);
    Page<Post> pagingRejectPost(Pageable page, Long memberId, Long reportCount);
    Page<Post> searchPost(Pageable page, Long memberId, Long reportCount, School school);

    Long getLikedCount(Post post);
}
