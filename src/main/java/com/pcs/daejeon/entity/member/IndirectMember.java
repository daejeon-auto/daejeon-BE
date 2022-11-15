package com.pcs.daejeon.entity.member;

import com.pcs.daejeon.entity.ReferCode;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity @Getter
public class IndirectMember extends Member {

    @OneToOne
    @JoinColumn(name = "refer_code_id")
    private ReferCode usedCode;
}
