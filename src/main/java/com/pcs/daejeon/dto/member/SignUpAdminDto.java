package com.pcs.daejeon.dto.member;

import com.pcs.daejeon.dto.school.SchoolRegistDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpAdminDto {

    private SignUpDto member;
    private SchoolRegistDto school;
}
