package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PunishRepository extends JpaRepository<Punish, Long> {

    List<Punish> findAllByMember(Member member);
}
