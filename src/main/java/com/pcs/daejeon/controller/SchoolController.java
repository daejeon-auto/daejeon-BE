package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.school.SchoolInfoDto;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping("/school/list")
    public ResponseEntity<Result<List<SchoolInfoDto>>> schoolList() {
        try {
            List<SchoolInfoDto> allSchool = schoolService.findAllSchool()
                    .stream()
                    .map(o -> new SchoolInfoDto(
                            o.getId(),
                            o.getName(),
                            o.getLocate()
                    )
            ).toList();
            return new ResponseEntity<>(new Result<>(allSchool, false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/school-info/{schoolId}")
    public ResponseEntity<Result<SchoolInfoDto>> schoolInfo(@PathVariable("schoolId") Long schoolId) {

        try {
            School school = schoolService.findSchool(schoolId);
            SchoolInfoDto schoolInfoDto = new SchoolInfoDto(schoolId,
                    school.getName(),
                    school.getLocate());

            return new ResponseEntity<>(new Result<>(schoolInfoDto, false), HttpStatus.OK);
        } catch (IllegalStateException e){
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found school")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(new Result<>(null, true), status);
        }
    }
}
