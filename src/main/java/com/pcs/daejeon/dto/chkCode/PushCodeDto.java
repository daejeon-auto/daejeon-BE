package com.pcs.daejeon.dto.chkCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushCodeDto {

    @NotEmpty
    private String phoneNumber;
}
