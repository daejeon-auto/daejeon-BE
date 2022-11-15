package com.pcs.daejeon.dto;

import com.pcs.daejeon.entity.type.AuthType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
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

    @NotNull
    private String password;

    @NotNull
    private String loginId;

    private String referCode = null;

    public SignUpDto(String name, String birthDay, String phoneNumber, AuthType authType, String studentNumber, String password, String loginId, String referCode) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.password = password;
        this.loginId = loginId;
        this.referCode = referCode;
    }

    public SignUpDto(String name, String birthDay, String phoneNumber, AuthType authType, String studentNumber, String pwd, String loginId) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.password = pwd;
        this.loginId = loginId;
    }
}
