package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.ReportListDto;
import com.pcs.daejeon.entity.Report;
import com.pcs.daejeon.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;

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
            System.out.println("e = " + e);
            return new ResponseEntity<>(new Result("", true), HttpStatus.BAD_REQUEST);
        }
    }
}
