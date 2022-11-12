package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.customMemberRepository.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
