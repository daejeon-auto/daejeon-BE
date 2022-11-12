package com.pcs.daejeon.dto;

import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class SignUpDto {
    @NotNull
    private String name;
    @NotNull
    private String birthDay;
    @NotNull
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @NotNull
    private String studentNumber;

    private MemberType memberType;

    @NotNull
    private String pwd;

    @NotNull
    private String loginId;
}
