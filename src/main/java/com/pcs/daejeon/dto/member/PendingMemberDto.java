package com.pcs.daejeon.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PendingMemberDto {

    private LocalDateTime created_date;
    private String birthday;
    private String name;
    private String std_number;
}
