package com.pcs.daejeon.dto.post;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class PostWriteDto {
    @NotEmpty @Size(min = 15, max = 100)
    private String description;
}
