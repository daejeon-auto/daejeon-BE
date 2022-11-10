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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public Result<Post> getPostPage(@PageableDefault(size = 20) Pageable pageable) {
        QueryResults<Post> post = postService.findPagedPost(pageable);
        Stream<PostDto> postDto = post.getResults()
                .stream()
                .map(o -> {
                    ZoneId seoulId = ZoneId.of("Asia/Seoul");
                    ZonedDateTime seoulTime = o.getCreatedDate().atZone(seoulId);
                    return new PostDto(
                            o.getId(),
                            o.getDescription(),
                            seoulTime,
                            o.getLiked()
                    );
                });
        Result<Post> postResult = new Result(postDto);

        return postResult;
    }

    @PostMapping("/post/write")
    public ResponseEntity<Result<String>> writePost(@RequestBody Post post) throws MalformedURLException {
        // TODO: is login

        if (post.validDescription()) {
            return new ResponseEntity<>(new Result<>("description's length is less then 5", true), HttpStatus.BAD_REQUEST);
        }

        try {
            Long postId = postService.writePost(post.getDescription());
            return new ResponseEntity<>(new Result<>("bad words", true), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
        }

        return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
    }

    @PostMapping("/post/accept/{id}")
    public ResponseEntity<Result<String>> acceptPost(@PathVariable("id") Long id) {
        try {
            postService.acceptPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<Result<String>>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/post/reject/{id}")
    public ResponseEntity<Result<String>> rejectedPost(@PathVariable("id") Long id) {
        try {
            postService.rejectPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<Result<String>>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/like/add/{id}")
    public ResponseEntity<Result> addLiked(@PathVariable("id") Long id) {

        try {
            postService.addLike(id);

            return new ResponseEntity<>(new Result("success"), HttpStatus.OK);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("e = " + e);
            return new ResponseEntity<>(new Result("server error", true), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
        private boolean hasError;

        public Result(T data) {
            this.data = data;
            this.hasError = false;
        }
    }
}
