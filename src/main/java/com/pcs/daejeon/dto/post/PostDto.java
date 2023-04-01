package com.pcs.daejeon.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class PostDto {

    private Long postId;
    private String description;
    private String created;
    private Long likedCount;
    private Boolean isLiked;
    private Boolean isReported;
}
