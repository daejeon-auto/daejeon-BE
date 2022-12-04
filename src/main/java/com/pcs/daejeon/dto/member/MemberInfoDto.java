package com.pcs.daejeon.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberInfoDto {

    private String name;
    private String std_num;
    private String birthDay;
    private String phoneNumber;
}
