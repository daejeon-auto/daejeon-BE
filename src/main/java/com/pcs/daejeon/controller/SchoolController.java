package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.school.SchoolListDto;
import com.pcs.daejeon.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping("/school/list")
    public ResponseEntity<Result<List<SchoolListDto>>> schoolList() {
        try {
            List<SchoolListDto> allSchool = schoolService.findAllSchool()
                    .stream()
                    .map(o -> new SchoolListDto(
                        o.getName(),
                        o.getLocate()
                    )
            ).toList();
            return new ResponseEntity<>(new Result<>(allSchool, false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
