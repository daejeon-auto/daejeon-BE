package com.pcs.daejeon.repository.sanction;

import com.pcs.daejeon.entity.sanction.ReportBullying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ReportBullyingRepository extends JpaRepository<ReportBullying, Long> {


}
