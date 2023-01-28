package com.pcs.daejeon.dto.member;

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
    private Long schoolId;

    @NotEmpty
    private String password;

    @NotEmpty
    private String loginId;

    private String referCode = null;

    public SignUpDto(String name, String birthDay, String phoneNumber, AuthType authType, String studentNumber, Long schoolId, String password, String loginId, String referCode) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.schoolId = schoolId;
        this.password = password;
        this.loginId = loginId;
        this.referCode = referCode;
    }

    public SignUpDto(String name, String birthDay, String phoneNumber, AuthType authType, String studentNumber, Long schoolId, String password, String loginId) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.authType = authType;
        this.studentNumber = studentNumber;
        this.schoolId = schoolId;
        this.password = password;
        this.loginId = loginId;
    }
}
