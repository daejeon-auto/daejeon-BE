package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.member.MemberListDto;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.dto.report.ReportListDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ReportService reportService;
    private final MemberService memberService;

    private final MemberRepository memberRepository;

    @PostMapping("/admin/reports/{id}")
    public ResponseEntity<Result> getReportList(@PathVariable("id") Long postId) {
        try {
            List<Report> reportList = reportService.getReportList(postId);
            Stream<ReportListDto> reportListDto = reportList.stream()
                    .map(o -> new ReportListDto(
                            o.getReason(),
                            o.getReportedBy().getId(),
                            o.getReportedAt()
                    ));

            return new ResponseEntity<>(new Result(reportListDto, false), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result("", true), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/admin/members")
    public ResponseEntity<Result> getMembers(@RequestParam(value = "memberId", required = false) Long memberId) {
        try {
            List<Member> members = memberService.getMembers(memberId);
            List<MemberListDto> memberListDto = members.stream()
                    .map(o -> new MemberListDto(o.getId(), o.getMemberType(), o.getUsedCode() != null ? o.getUsedCode().getCode() : ""))
                    .toList();

            return new ResponseEntity<>(new Result(memberListDto, false), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/members/pending")
    public ResponseEntity<Result> getPendingMembers() {
        try {
            List<Member> pendingMembers = memberService.getPendingMembers();

            List<PendingMemberDto> pendingMemberDtos = pendingMembers.stream()
                    .map(o -> new PendingMemberDto(
                            o.getCreatedDate(),
                            o.getBirthDay(),
                            o.getName(),
                            o.getStudentNumber()))
                    .toList();
            return new ResponseEntity<>(new Result(pendingMemberDtos, false), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/pending-member/accept")
    public ResponseEntity<Result> acceptPendingMember(@RequestBody @Valid PendingMemberDto pendingMemberDto) {

        try {
            memberService.acceptPendingMember(pendingMemberDto);

            log.info("[accept-pending-member] accept userName = "+pendingMemberDto.getName()+" | student number = "
                    +pendingMemberDto.getStd_number()+" || by adminId = "+memberRepository.getLoginMember().getId());
            return new ResponseEntity<>(new Result("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result( "member not found", true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/admin/pending-member/reject")
    public ResponseEntity<Result> rejectPendingMember(@RequestBody @Valid PendingMemberDto pendingMemberDto) {

        try {
            memberService.rejectPendingMember(pendingMemberDto);

            log.info("[reject-pending-member] reject userName = "+pendingMemberDto.getName()+" | student number = "
                    +pendingMemberDto.getStd_number()+" || by adminId = "+memberRepository.getLoginMember().getId());
            return new ResponseEntity<>(new Result("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result( "member not found", true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }
}
