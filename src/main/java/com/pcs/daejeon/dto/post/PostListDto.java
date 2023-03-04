package com.pcs.daejeon.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostListDto {
    private List<PostDto> postList;
    private long totalPost;
    private long totalPage;
}
