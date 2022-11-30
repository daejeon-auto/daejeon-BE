package com.pcs.daejeon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportListDto {
    private String reason;
    private Long reportedBy;
    private LocalDateTime reportedAt;
}
