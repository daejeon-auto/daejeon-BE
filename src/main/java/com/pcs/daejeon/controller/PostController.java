package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.post.*;
import com.pcs.daejeon.dto.report.ReportReasonDto;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.QLike;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.entity.QReport;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.service.PostService;
import com.pcs.daejeon.service.ReportService;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ReportService reportService;

    @PostMapping("/posts")
    public ResponseEntity<Result<PostListDto>> getPostPage(@PageableDefault(size = 15) Pageable pageable) {
        Page<Tuple> posts = postService.findPagedPost(pageable);

        Stream<PostDto> postDto = posts.getContent()
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
        Result<PostListDto> postResult = new Result<>(new PostListDto(
                postDto,
                posts.getTotalElements(),
                posts.getTotalPages()
        ));

        boolean isLogin = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                isLogin = true;
            }
        }

        return ResponseEntity
                .ok().header("isLogin", Boolean.toString(isLogin))
                .body(postResult);
    }

    @PostMapping("/post/write")
    public ResponseEntity<Result<String>> writePost(@RequestBody @Valid Post post) {
        try {
            postService.writePost(post.getDescription());

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>("bad words", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/post/report/{id}")
    public ResponseEntity<Result<String>> reportPost(@PathVariable("id") Long postId, @RequestBody @Valid ReportReasonDto reason) {

        try {
            reportService.report(reason.getReason(), postId);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>("post not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>("bad request"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/post/accept/{id}")
    public ResponseEntity<Result<String>> acceptPost(@PathVariable("id") Long id) {
        try {
            postService.acceptPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/admin/post/reject/{id}")
    public ResponseEntity<Result<String>> rejectedPost(@PathVariable("id") Long id) {
        try {
            postService.deletePost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/like/add/{id}")
    public ResponseEntity<Result<String>> addLiked(@PathVariable("id") Long id) {

        try {
            postService.addLike(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            if (Objects.equals(e.getMessage(), "member already liked this post")) {
                return new ResponseEntity<>(new Result<>(null, true), HttpStatus.CONFLICT);
            } else if (Objects.equals(e.getMessage(), "post not found")) {
                return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
            }

            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException | URISyntaxException e) {
            log.error("e = " + e);
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/admin/posts/reject")
    public ResponseEntity<Result<Stream<RejectedPostDto>>> rejectPostList(
            @PageableDefault(size = 15) Pageable pageable,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "reportCount", required = false) Long reportCount) {

        try {
            Page<Post> pagedRejectedPost = postService.findPagedRejectedPost(pageable, memberId, reportCount);

            Stream<RejectedPostDto> result = pagedRejectedPost.getContent()
                    .stream()
                    .map(o -> new RejectedPostDto(
                            o.getId(),
                            o.getDescription(),
                            o.getCreatedDate(),
                            o.getUpdatedDate(),
                            o.getReports().size(),
                            o.getCreatedBy()
                    ));
            return new ResponseEntity<>(new Result<>(result, false), HttpStatus.OK);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/member/posts")
    public ResponseEntity<Result<MyPostListDto>> getWrotePosts(@PageableDefault Pageable pageable) {

         try {
             Page<Post> pagedPostByMemberId = postService.findPagedPostByMemberId(pageable);

             List<MyPostDto> postDtos = pagedPostByMemberId.getContent().stream()
                 .map(o -> new MyPostDto(
                             Objects.requireNonNull(o).getId(),
                             o.getDescription(),
                             o.getCreatedDate(),
                             postRepository.getLikedCount(o)
                     )).toList();

             MyPostListDto myPostListDto = new MyPostListDto(postDtos,
                     pagedPostByMemberId.getTotalElements(),
                     pagedPostByMemberId.getTotalPages());
             return new ResponseEntity<>(new Result<>(myPostListDto,false), HttpStatus.OK);
         } catch (Exception e) {
             log.error("e = " + e);
             return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
         }
    }
}
