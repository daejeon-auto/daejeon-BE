package com.pcs.daejeon.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class PostDto {

    private Long postId;
    private String description;
    private LocalDateTime created;

    public PostDto(Long postId, String description, LocalDateTime created) {
        this.postId = postId;
        this.description = description;
        this.created = created;
    }
}
