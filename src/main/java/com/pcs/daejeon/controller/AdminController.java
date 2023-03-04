package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.MemberListDto;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.dto.member.PersonalInfo;
import com.pcs.daejeon.dto.post.RejectedPostDto;
import com.pcs.daejeon.dto.report.ReportListDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.PostService;
import com.pcs.daejeon.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final ReportService reportService;
    private final MemberService memberService;
    private final PostService postService;
    private final Util util;

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


    @PostMapping("/admin/members")
    public ResponseEntity<Result<List<MemberListDto>>> getMembers(
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "onlyAdmin", required = false) boolean onlyAdmin) {

        try {
            List<Member> members = memberService.getMembers(memberId, onlyAdmin);
            List<MemberListDto> memberListDto = members.stream()
                    .map(o -> new MemberListDto(o.getId(), o.getMemberType(), o.getUsedCode() != null ? o.getUsedCode().getCode() : ""))
                    .toList();

            return new ResponseEntity<>(new Result<>(memberListDto, false), HttpStatus.OK);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/members/pending")
    public ResponseEntity<Result<List<PendingMemberDto>>> getPendingMembers() {
        try {
            List<Member> pendingMembers = memberService.getPendingMembers();

            List<PendingMemberDto> pendingMemberDtos = pendingMembers.stream()
                    .map(o -> new PendingMemberDto(
                            o.getSchool().getId(),
                            o.getBirthDay(),
                            o.getName(),
                            o.getStudentNumber()))
                    .toList();
            return new ResponseEntity<>(new Result<>(pendingMemberDtos, false), HttpStatus.OK);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/pending-member/accept")
    public ResponseEntity<Result<String>> acceptPendingMember(@RequestBody @Valid PendingMemberDto pendingMemberDto) {

        try {
            memberService.acceptPendingMember(pendingMemberDto);

            log.info("[accept-pending-member] accept userName = "+pendingMemberDto.getName()+" | student number = "
                    +pendingMemberDto.getStd_number()+" || by adminId = "+util.getLoginMember().getId());
            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/admin/pending-member/reject")
    public ResponseEntity<Result<String>> rejectPendingMember(@RequestBody @Valid PendingMemberDto pendingMemberDto) {

        try {
            memberService.rejectPendingMember(pendingMemberDto);

            log.info("[reject-pending-member] reject userName = "+pendingMemberDto.getName()+" | student number = "
                    +pendingMemberDto.getStd_number()+" || by adminId = "+util.getLoginMember().getId());
            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/personal-info/{id}")
    public ResponseEntity<Result<PersonalInfo>> callPersonalInfo(@PathVariable("id") Long memberId) {

        try {
            Member member = memberService.findPersonalInfo(memberId);

            PersonalInfo personalInfo = new PersonalInfo(
                    member.getId(),
                    member.getCreatedDate(),
                    member.getBirthDay(),
                    member.getName(),
                    member.getPhoneNumber(),
                    member.getStudentNumber(),
                    member.getRole(),
                    member.getUsedCode() != null ? member.getUsedCode().getCode() : ""
            );

            log.info("[call-personal-info] call by adminId = "+util.getLoginMember().getId());
            return new ResponseEntity<>(new Result<>(personalInfo, false), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/member/set-role/{id}/{role}")
    public ResponseEntity<Result<String>> setRole(@PathVariable("id") Long memberId, @PathVariable("role") RoleTier tier) {

        try {
            Member member = memberService.setMemberRole(memberId, tier);

            log.info("[set-role] set role memberId = "+member.getId()+" changed role = "+member.getRole().toString()+" by adminId = "+util.getLoginMember().getId());
            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>("server error", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/posts")
    public ResponseEntity<Result<Stream<RejectedPostDto>>> rejectPostList(
            @PageableDefault(size = 15) Pageable pageable,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "reportCount", required = false) Long reportCount) {

        try {
            Page<Post> searchPost = postService.searchPost(pageable, memberId, reportCount);

            Stream<RejectedPostDto> result = searchPost.getContent()
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
