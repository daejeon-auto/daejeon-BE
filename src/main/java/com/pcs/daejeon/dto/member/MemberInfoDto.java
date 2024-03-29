package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.entity.sanction.Punish;
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
    private List<Punish> punishes;
}
