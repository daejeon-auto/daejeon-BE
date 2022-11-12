package com.pcs.daejeon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Setter @Getter
public class PostDto {

    private Long postId;
    private String description;
    private LocalDateTime created;
    private int liked;
}
