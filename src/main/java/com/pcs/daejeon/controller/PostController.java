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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public Result<Post> getPostPage(@PageableDefault(size = 20) Pageable pageable) {
        QueryResults<Post> post = postService.findPagedPost(pageable);
        Stream<PostDto> postDto = post.getResults()
                .stream()
                .map(o -> new PostDto(
                        o.getId(),
                        o.getDescription(),
                        o.getCreatedDate(),
                        o.getLiked()
                ));
        Result<Post> postResult = new Result(postDto);

        return postResult;
    }

    @PostMapping("/post/write")
    public Result<String> writePost(@RequestBody Post post) throws MalformedURLException {
        // TODO: is login

        Long postId = postService.writePost(post.getDescription());

        return new Result<>("success");
    }

    @PostMapping("/post/accept/{id}")
    public ResponseEntity<Result<String>> acceptPost(@PathVariable("id") Long id) {
        try {
            postService.acceptPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<Result<String>>(new Result<>("error on api server"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/post/reject/{id}")
    public ResponseEntity<Result<String>> rejectedPost(@PathVariable("id") Long id) {
        try {
            postService.rejectPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<Result<String>>(new Result<>("error on api server"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/like/add/{id}")
    public ResponseEntity<Result> addLiked(@PathVariable("id") Long id) {

        try {
            postService.addLike(id);

            return new ResponseEntity<>(new Result("success"), HttpStatus.OK);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("e = " + e);
            return new ResponseEntity<>(new Result("server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
