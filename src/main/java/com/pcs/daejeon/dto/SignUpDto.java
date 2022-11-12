package com.pcs.daejeon.dto;

import com.pcs.daejeon.entity.type.AuthType;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private String pwd;

    @NotNull
    private String loginId;

    public SignUpDto(String name, String birthDay, String phoneNumber, AuthType authType, String studentNumber, String pwd, String loginId) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.pwd = pwd;
        this.loginId = loginId;
    }
}
