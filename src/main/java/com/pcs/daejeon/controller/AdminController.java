package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.MemberListDto;
import com.pcs.daejeon.dto.ReportListDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AdminController {

    private final ReportService reportService;
    private final MemberService memberService;

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
            List<Member> members = memberService.getMembers();
            List<MemberListDto> memberListDto = members.stream()
                    .map(o -> new MemberListDto(o.getId(), o.getUsedCode().toString()))
                    .toList();

            return new ResponseEntity<>(new Result(memberListDto, false), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }
}
