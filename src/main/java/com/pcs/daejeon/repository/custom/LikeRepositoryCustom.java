package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Member;

public interface LikeRepositoryCustom {


    /**
     * 동일 값이 있을 때 true
     * 아니면 false
     */
    boolean validLike(Member member, Long postId);

}
