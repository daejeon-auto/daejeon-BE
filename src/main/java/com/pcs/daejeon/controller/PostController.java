package com.pcs.daejeon.controller;

import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.service.PostService;
import com.querydsl.core.QueryResults;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public Result getPostPage(Pageable pageable) {
        QueryResults<Post> post = postService.getPost(pageable);
        Result<Post> postResult = new Result(post);

        return postResult;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
