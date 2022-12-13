package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.entity.type.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberInfoDto {

    private String name;
    private String std_num;
    private String birthDay;
    private String phoneNumber;
    private AuthType auth_type;
}
