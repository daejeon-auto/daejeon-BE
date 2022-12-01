package com.pcs.daejeon.dto;

import com.pcs.daejeon.entity.type.MemberType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberListDto {

    private Long id;
    private MemberType status;
    private String refer_code;
}
