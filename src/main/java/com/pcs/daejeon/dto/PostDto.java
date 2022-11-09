package com.pcs.daejeon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Setter @Getter
public class PostDto {

    private Long postId;
    private String description;
    private ZonedDateTime created;
    private int liked;
}
