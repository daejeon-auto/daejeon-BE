package com.pcs.daejeon.dto.member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingMemberDto {

    @NotNull
    private Long schoolId;
    @NotNull
    private String birthday;
    @NotNull
    private String name;
    @NotNull
    private String std_number;
}
