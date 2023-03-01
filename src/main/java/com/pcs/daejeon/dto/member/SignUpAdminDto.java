package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.dto.school.SchoolRegistDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpAdminDto {

    @NotNull
    private SignUpDto member;
    @NotNull
    private SchoolRegistDto school;
}
