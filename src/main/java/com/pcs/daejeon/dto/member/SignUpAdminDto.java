package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.dto.school.SchoolRegistDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpAdminDto {

    private SignUpDto member;
    private SchoolRegistDto school;
}
