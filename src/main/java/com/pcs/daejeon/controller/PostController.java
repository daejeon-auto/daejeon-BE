package com.pcs.daejeon.controller;

import com.pcs.daejeon.dto.PostDto;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.service.PostService;
import com.querydsl.core.QueryResults;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public Result getPostPage(@PageableDefault(size = 20) Pageable pageable) {
        QueryResults<Post> post = postService.findPagedPost(pageable);
        Stream<PostDto> postDto = post.getResults()
                .stream()
                .map(o -> new PostDto(
                        o.getId(),
                        o.getDescription(),
                        o.getCreatedDate()
                ));
        Result<Post> postResult = new Result(postDto);

        return postResult;
    }

    @PostMapping("/post/write")
    public Result writePost(@RequestBody Post post) throws MalformedURLException {
        // TODO: is login

        Long postId = postService.writePost(post.getDescription());

        return new Result("success");
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
