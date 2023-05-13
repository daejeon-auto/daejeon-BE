package com.pcs.daejeon.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePwdDto {

    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
