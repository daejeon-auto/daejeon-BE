package com.pcs.daejeon.dto.sanction.report;


import com.pcs.daejeon.entity.type.ReportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ReportReasonDto {
    @NotEmpty @Size(min = 10, max = 500)
    private String reason;

    @NotNull
    private ReportType reportType;

    public ReportReasonDto(String reason, ReportType reportType) {
        this.reason = reason;
        this.reportType = reportType;
    }
}
