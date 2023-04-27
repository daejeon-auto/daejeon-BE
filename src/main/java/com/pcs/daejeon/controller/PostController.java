package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.post.*;
import com.pcs.daejeon.dto.report.ReportReasonDto;
import com.pcs.daejeon.entity.*;
import com.pcs.daejeon.entity.type.PunishRating;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.service.PostService;
import com.pcs.daejeon.service.PunishService;
import com.pcs.daejeon.service.ReportService;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    private final PunishService punishService;
    private final Util util;

    @PostMapping("/posts")
    public ResponseEntity<Result<PostListDto>> getPostPage(@PageableDefault(size = 15) Pageable pageable,
                                                           @RequestParam(value = "schoolId", required = false) Long schoolId) {

        try {
            if (schoolId == null || schoolId == 0) {
                if (util.getLoginMember() != null) {
                    schoolId = util.getLoginMember()
                                    .getSchool().getId();
                } else {
                    throw new IllegalStateException("need schoolId");
                }
            }

            Page<Tuple> posts = postService.findPagedPost(pageable, schoolId);

            List<PostDto> postDto = posts.getContent()
                    .stream()
                    .map(o -> {
                        Post post = o.get(QPost.post);

                        boolean isLiked = o.get(QLike.like) != null;
                        boolean isReported = o.get(QReport.report) != null;

                        return new PostDto(
                                Objects.requireNonNull(post).getId(),
                                post.getDescription(),
                                post.getCreatedDate().toString(),
                                postRepository.getLikedCount(post),
                                isLiked,
                                isReported
                        );
                    }).toList();
            Result<PostListDto> postResult = new Result<>(new PostListDto(
                    postDto,
                    posts.getTotalElements(),
                    posts.getTotalPages()
            ));

            return ResponseEntity.ok().body(postResult);
        } catch (IllegalStateException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (Objects.equals(e.getMessage(), "not found school")) status = HttpStatus.NOT_FOUND;
            if (Objects.equals(e.getMessage(), "need schoolId")) status = HttpStatus.UNAUTHORIZED;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/write")
    public ResponseEntity<Result<String>> writePost(@RequestBody @Valid PostWriteDto post) {
        try {
            Long memberId = util.getLoginMember().getId();
            List<Punish> activePunish = punishService.getActivePunish(memberId);

            activePunish.forEach(val -> {
                if (val.getRating().equals(PunishRating.WRITE_DENY))
                    throw new IllegalArgumentException("limited account");
            });

            postService.writePost(post.getDescription());

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalArgumentException | MethodArgumentNotValidException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;

            if (Objects.equals(e.getMessage(), "limited account")) status = HttpStatus.FORBIDDEN;

            return new ResponseEntity<>(new Result<>(status == HttpStatus.FORBIDDEN ?
                    "계정이 일부 기능 정지 상태입니다." : null, true), status);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/report/{id}")
    public ResponseEntity<Result<String>> reportPost(@PathVariable("id") Long postId,
                                                     @RequestBody @Valid ReportReasonDto reason) {

        try {
            reportService.report(reason.getReason(), postId);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (InvalidDataAccessApiUsageException e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found post")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/like/add/{id}")
    public ResponseEntity<Result<String>> addLiked(@PathVariable("id") Long id) {

        try {
            postService.addLike(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
            if (Objects.equals(e.getMessage(), "member already liked this post")) status = HttpStatus.CONFLICT;
            if (Objects.equals(e.getMessage(), "post not found")) status = HttpStatus.NOT_FOUND;
            if (e.getMessage().equals("school is different")) status = HttpStatus.FORBIDDEN;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (IOException | URISyntaxException e) {
            log.error("e = " + e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
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
