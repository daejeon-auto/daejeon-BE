package com.pcs.daejeon.dto.chkCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChkCodeDto {

    @NotEmpty
    private String phoneNumber;

    @NotNull
    private int code;
}
