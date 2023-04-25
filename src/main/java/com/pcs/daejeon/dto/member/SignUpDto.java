package com.pcs.daejeon.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class SignUpDto {
    @NotEmpty @Size(max = 11)
    private String phoneNumber;

    @NotNull
    private Long schoolId;

    @NotEmpty @Size(min = 8)
    private String password;

    @NotEmpty @Size(min = 5)
    private String loginId;

    public SignUpDto(String phoneNumber, Long schoolId, String password, String loginId) {
        this.phoneNumber = phoneNumber;
        this.schoolId = schoolId;
        this.password = password;
        this.loginId = loginId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
}
