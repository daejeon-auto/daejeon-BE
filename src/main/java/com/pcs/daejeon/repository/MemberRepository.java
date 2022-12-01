package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Member findByLoginId(String id);

    List<Member> findAllByMemberTypeOrderByCreatedDateAsc(MemberType memberType);
}
