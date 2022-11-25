package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.custom.ReportRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
    Report findByIdAndReportedByIs(Long id, Member reportedBy);
}
