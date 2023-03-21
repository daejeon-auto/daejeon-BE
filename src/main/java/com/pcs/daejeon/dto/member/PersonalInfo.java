package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.entity.type.RoleTier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalInfo {

    private Long memberId;
    private LocalDateTime created_date;
    private String phone_num;
    private RoleTier role;
}
