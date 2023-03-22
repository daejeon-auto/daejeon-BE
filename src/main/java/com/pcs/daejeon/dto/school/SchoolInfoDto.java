package com.pcs.daejeon.dto.school;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchoolInfoDto {
    private Long id;
    private String name;
    private String locate;
}
