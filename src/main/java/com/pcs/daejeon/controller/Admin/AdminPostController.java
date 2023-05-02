package com.pcs.daejeon.controller.Admin;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.post.RejectedPostDto;
import com.pcs.daejeon.dto.report.ReportListDto;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.service.PostService;
import com.pcs.daejeon.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AdminPostController {

    private final ReportService reportService;
    private final PostService postService;

    @PostMapping("/admin/reports/{id}")
    public ResponseEntity<Result<Stream<ReportListDto>>> getReportList(@PathVariable("id") Long postId) {
        try {
            List<Report> reportList = reportService.getReportList(postId);
            Stream<ReportListDto> reportListDto = reportList.stream()
                    .map(o -> new ReportListDto(
                            o.getReason(),
                            o.getReportedBy().getId(),
                            o.getReportedAt()
                    ));

            return new ResponseEntity<>(new Result<>(reportListDto, false), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found post")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
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

    @PostMapping("/admin/post/accept/{id}")
    public ResponseEntity<Result<String>> acceptPost(@PathVariable("id") Long id) {
        try {
            postService.acceptPost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found post")) status = HttpStatus.NOT_FOUND;
            if (e.getMessage().equals("school is different")) status = HttpStatus.FORBIDDEN;

            return new ResponseEntity<>(new Result<>(e.getMessage(), true), status);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/admin/post/reject/{id}")
    public ResponseEntity<Result<String>> rejectedPost(@PathVariable("id") Long id) {
        try {
            postService.deletePost(id);

            return new ResponseEntity<>(new Result<>("success"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found post")) status = HttpStatus.NOT_FOUND;
            if (e.getMessage().equals("school is different")) status = HttpStatus.FORBIDDEN;

            return new ResponseEntity<>(new Result<>(e.getMessage(), true), status);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new Result<>("error on api server", true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
