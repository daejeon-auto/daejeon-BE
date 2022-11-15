package com.pcs.daejeon.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.ReferCode;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class DirectMember extends Member {

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<ReferCode> referCodes = new ArrayList<>();
}
