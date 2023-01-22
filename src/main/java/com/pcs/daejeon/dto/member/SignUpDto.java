package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.AuthType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class SignUpDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String birthDay;
    @NotEmpty
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @NotEmpty
    private String studentNumber;

    @NotNull
    private School school;

    @NotEmpty
    private String password;

    @NotEmpty
    private String loginId;

    private String referCode = null;

    public SignUpDto(String name,
                     String birthDay,
                     String phoneNumber,
                     AuthType authType,
                     String studentNumber,
                     String password,
                     String loginId,
                     String schoolName,
                     String locate,
                     String instaId,
                     String instaPwd,
                     String referCode) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.password = password;
        this.loginId = loginId;
        this.referCode = referCode;
        this.school = new School(schoolName, locate, instaId, instaPwd);
    }

    public SignUpDto(String name,
                     String birthDay,
                     String phoneNumber,
                     AuthType authType,
                     String studentNumber,
                     String pwd,
                     String loginId,
                     String schoolName,
                     String locate,
                     String instaId,
                     String instaPwd) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.password = pwd;
        this.loginId = loginId;
        this.school = new School(schoolName, locate, instaId, instaPwd);
    }
}
