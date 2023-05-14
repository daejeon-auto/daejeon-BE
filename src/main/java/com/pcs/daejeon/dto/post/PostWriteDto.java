package com.pcs.daejeon.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class PostWriteDto {
    @NotEmpty @Size(min = 10, max = 100)
    private String description;

    @NotNull
    private Long schoolId;
}
