package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.PostDto;
import com.pcs.daejeon.dto.PostListDto;
import com.pcs.daejeon.dto.RejectedPostDto;
import com.pcs.daejeon.dto.ReportReasonDto;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.QLike;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.entity.QReport;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.service.PostService;
import com.pcs.daejeon.service.ReportService;
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
    private final PostRepository postRepository;
    private final ReportService reportService;

    @PostMapping("/posts")
    public Result<Post> getPostPage(@PageableDefault(size = 15) Pageable pageable) {
        QueryResults<Tuple> posts = postService.findPagedPost(pageable);

        Stream<PostDto> postDto = posts.getResults()
                .stream()
                .map(o -> {
                    Post post = o.get(QPost.post);

                    boolean isLiked = false;
                    boolean isReported = false;

                    if (o.get(QLike.like) != null) {
                        isLiked = true;
                    }
                    if (o.get(QReport.report) != null) {
                        isReported = true;
                    }


                    return new PostDto(
                            Objects.requireNonNull(post).getId(),
                            post.getDescription(),
                            post.getCreatedDate(),
                            postRepository.getLikedCount(post),
                            isLiked,
                            isReported
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
    public ResponseEntity<Result<String>> reportPost(@PathVariable("id") Long postId, @RequestBody @Valid ReportReasonDto reason) {

        try {
            reportService.report(reason.getReason(), postId);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>("post not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println("e = " + e);
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
            postService.deletePost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<Result<String>>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/like/add/{id}")
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

    @PostMapping("/admin/posts/reject")
    public ResponseEntity<Result> rejectPostList(
            @PageableDefault(size = 15) Pageable pageable,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "reportCount", required = false) Long reportCount) {

        try {
            QueryResults<Post> pagedRejectedPost = postService.findPagedRejectedPost(pageable, memberId, reportCount);

            Stream<RejectedPostDto> result = pagedRejectedPost.getResults()
                    .stream()
                    .map(o -> new RejectedPostDto(
                            o.getId(),
                            o.getDescription(),
                            o.getCreatedDate(),
                            o.getUpdatedDate(),
                            o.getReports().size(),
                            o.getCreatedBy()
                    ));
            return new ResponseEntity<>(new Result(result, false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result("failed", true), HttpStatus.BAD_REQUEST);
        }
    }
}
