package com.pcs.daejeon.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class PostListDto {
    private Stream<PostDto> postList;
    private long totalPost;
    private long totalPage;
}
