package com.pcs.daejeon.dto.school;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchoolListDto {
    private Long id;
    private String name;
    private String locate;
}
