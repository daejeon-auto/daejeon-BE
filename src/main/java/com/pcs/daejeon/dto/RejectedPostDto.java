package com.pcs.daejeon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class RejectedPostDto {

    private Long postId;
    private String description;
    private LocalDateTime created;
    private LocalDateTime updated;

    public RejectedPostDto(Long postId, String description, LocalDateTime created, LocalDateTime updated) {
        this.postId = postId;
        this.description = description;
        this.created = created;
        this.updated = updated;
    }
}
