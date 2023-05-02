package com.pcs.daejeon.dto.report;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddReportBullyingDto {

    @NotEmpty
    private String reason;

    @NotEmpty
    private String punishmentLevel;
}
