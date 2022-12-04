package com.pcs.daejeon.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyPostDto {

    private Long postId;
    private String description;
    private LocalDateTime created;
    private Long likedCount;
}
