package com.pcs.daejeon.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ReportReasonDto {
    @NotNull
    private String reason;

    public ReportReasonDto(String reason) {
        this.reason = reason;
    }
}
