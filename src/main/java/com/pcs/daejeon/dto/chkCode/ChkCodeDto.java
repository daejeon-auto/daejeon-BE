package com.pcs.daejeon.dto.chkCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChkCodeDto {

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private int code;
}
