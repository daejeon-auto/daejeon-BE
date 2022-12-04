package com.pcs.daejeon.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReferCodeDto {

    private Long code_id;
    private String code;
    private String used_by;
    private LocalDateTime created_at;
    private Boolean isUsed;
}
