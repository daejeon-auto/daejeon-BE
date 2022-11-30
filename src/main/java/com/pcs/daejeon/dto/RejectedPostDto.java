package com.pcs.daejeon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter @Setter
public class RejectedPostDto {

    private Long postId;
    private String description;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer reportedCount;
    private String createdBy;
}
