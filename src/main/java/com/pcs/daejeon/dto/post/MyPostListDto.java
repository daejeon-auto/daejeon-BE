package com.pcs.daejeon.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MyPostListDto {

    private List<MyPostDto> postList;
    private long totalPost;
    private long totalPage;
}
