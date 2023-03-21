package com.pcs.daejeon.dto.chkCode;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class ChkCodeDto {

    @NotEmpty
    String phoneNumber;

    @NotEmpty
    int code;
}
