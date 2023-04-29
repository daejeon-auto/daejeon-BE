package com.pcs.daejeon.dto.school;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class InstaInfoUpdateDto {

    @NotEmpty
    private String instaId;
    @NotEmpty
    private String instaPwd;
}
