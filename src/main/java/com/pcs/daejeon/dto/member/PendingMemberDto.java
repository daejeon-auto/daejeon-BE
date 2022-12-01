package com.pcs.daejeon.dto.member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingMemberDto {

    @NotNull
    private LocalDateTime created_date;
    @NotNull
    private String birthday;
    @NotNull
    private String name;
    @NotNull
    private String std_number;
}
