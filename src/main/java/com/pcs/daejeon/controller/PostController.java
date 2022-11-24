package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.PostDto;
import com.pcs.daejeon.dto.PostListDto;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.QLike;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.service.PostService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public Result<Post> getPostPage(@PageableDefault(size = 15) Pageable pageable) {
        QueryResults<Tuple> posts = postService.findPagedPost(pageable);

        Stream<PostDto> postDto = posts.getResults()
                .stream()
                .map(o -> {
                    Post post = o.get(QPost.post);
                    if (o.get(QLike.like) == null) {
                        return new PostDto(
                                Objects.requireNonNull(post).getId(),
                                post.getDescription(),
                                post.getCreatedDate()
                        );
                    }
                    return new PostDto(
                            Objects.requireNonNull(post).getId(),
                            post.getDescription(),
                            post.getCreatedDate(),
                            o.get(QLike.like).getPost().getId()
                    );
                });
        Result<Post> postResult = new Result(new PostListDto(
                postDto,
                posts.getTotal(),
                (posts.getTotal() / 20) + 1
        ));

        return postResult;
    }

    @PostMapping("/post/write")
    public ResponseEntity<Result<String>> writePost(@RequestBody @Valid Post post) throws MalformedURLException {
        try {
            Long postId = postService.writePost(post.getDescription());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Result<>("bad words", true), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
    }

    @PostMapping("/post/report/{id}")
    public ResponseEntity<Result<String>> reportPost(@PathVariable("id") Long postId) {

        try {
            postService.reportPost(postId);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>("post not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result<>("bad request"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/post/accept/{id}")
    public ResponseEntity<Result<String>> acceptPost(@PathVariable("id") Long id) {
        try {
            postService.acceptPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<Result<String>>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/admin/post/reject/{id}")
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

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            if (Objects.equals(e.getMessage(), "member already liked this post")) {
                return new ResponseEntity<>(new Result("already liked", true), HttpStatus.BAD_REQUEST);
            } else if (Objects.equals(e.getMessage(), "post not found")) {
                return new ResponseEntity<>(new Result("post not found", true), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Result("server error", true), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
