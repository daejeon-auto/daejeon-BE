package com.pcs.daejeon.dto.sanction.report;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ReportReasonDto {
    @NotEmpty @Size(min = 10, max = 500)
    private String reason;

    public ReportReasonDto(String reason) {
        this.reason = reason;
    }
}
