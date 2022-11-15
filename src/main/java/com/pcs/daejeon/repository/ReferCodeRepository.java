package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ReferCodeRepository extends JpaRepository<ReferCode, Long> {
    @Query("select rc from ReferCode rc where rc.isUsed=false and rc.code = :code")
    ReferCode findUnusedReferCode(@Param("code") String code);

    @Query("select count(rc) from ReferCode rc where rc.createdBy = :member")
    Long findReferCodeCount(@Param("member") Member member);

    @Query("select rc from ReferCode rc where rc.createdBy = :member and rc.isUsed = false")
    List<ReferCode> findAllByCodeList(@Param("member") Member member);
}
