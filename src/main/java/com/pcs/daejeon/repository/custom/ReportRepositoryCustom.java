package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Report;

public interface ReportRepositoryCustom {
    /**
     * if already reported, return false
     * if you can create report, return true
     */
    boolean validReport();
}
