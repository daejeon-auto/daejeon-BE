package com.pcs.daejeon.dto.report;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class ReportReasonDto {
    @NotEmpty @Min(10) @Max(500)
    private String reason;

    public ReportReasonDto(String reason) {
        this.reason = reason;
    }
}
