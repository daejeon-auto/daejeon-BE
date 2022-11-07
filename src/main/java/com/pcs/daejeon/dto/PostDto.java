package com.pcs.daejeon.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
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
