package com.pcs.daejeon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@Setter @Getter
public class PostDto {

    private Long postId;
    private String description;
    private LocalDateTime created;
    private Long likedPostId;

    public PostDto(Long postId, String description, LocalDateTime created) {
        this.postId = postId;
        this.description = description;
        this.created = created;
    }
}
