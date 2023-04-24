package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.entity.type.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class MemberInfoDto {

    private String phoneNumber;
    private String schoolName;
    private String schoolLocate;
    private AuthType auth_type;
    private List<Punish> punishes;
}
