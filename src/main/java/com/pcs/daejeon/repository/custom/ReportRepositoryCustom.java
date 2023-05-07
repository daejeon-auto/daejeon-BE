package com.pcs.daejeon.repository.custom;

public interface ReportRepositoryCustom {
    /**
     * if already reported, return false
     * if you can create report, return true
     */
    boolean validReport(Long postId);
}
