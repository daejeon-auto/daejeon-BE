package com.pcs.daejeon.dto.school;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class SchoolRegistDto {

    @Size(min = 3) @NotEmpty
    private String name;

    @NotEmpty
    private String locate;

    @NotEmpty
    private String instaId;

    @NotEmpty
    private String instaPwd;
}
