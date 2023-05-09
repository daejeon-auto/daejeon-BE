package com.pcs.daejeon.controller;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGResponseException;
import com.pcs.daejeon.common.InstagramUtil;
import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.school.InstaInfoUpdateDto;
import com.pcs.daejeon.dto.school.MealDto;
import com.pcs.daejeon.dto.school.SchoolInfoDto;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.SchoolRepository;
import com.pcs.daejeon.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SchoolController {

    private final SchoolService schoolService;
    private final SchoolRepository schoolRepository;
    private final Util util;

    @PostMapping("/school/meal")
    public ResponseEntity<Result> schoolMeal() {
        try {
            String schoolCode = util.getLoginMember().getSchool().getCode();
            String locationCode = util.getLoginMember().getSchool().getLocationCode();
            MealDto mealServiceInfo = schoolService.getMealServiceInfo(schoolCode, locationCode);

            return new ResponseEntity<>(new Result<>(mealServiceInfo, false), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("e = " + e);
            return new ResponseEntity<>(new Result<>("", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
            log.error(e.getMessage());
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

    @PostMapping("/admin/update-insta-info")
    public ResponseEntity<Result> updateInstaInfo(@RequestBody @Valid InstaInfoUpdateDto instaInfoUpdateDto) {

        try {
            IGClient c = IGClient.builder()
                    .username(instaInfoUpdateDto.getInstaId())
                    .password(instaInfoUpdateDto.getInstaPwd())
                    .login();

            schoolService.updateInstaInfo(
                    instaInfoUpdateDto.getInstaId(),
                    instaInfoUpdateDto.getInstaPwd());

            return new ResponseEntity<>(new Result<>(null, false), HttpStatus.OK);
        } catch (IllegalStateException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found school")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (IGResponseException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(new Result<>("instagram id or password is not exist", true), status);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), status);
        }
    }
    @PostMapping("/admin/disable-insta")
    public ResponseEntity<Result> disableInsta() {

        try {
            schoolService.disableInsta();

            return new ResponseEntity<>(new Result<>(null, false), HttpStatus.OK);
        } catch (IllegalStateException e){
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found school")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), status);
        }
    }

    @PostMapping("/admin/school/active-meal")
    public ResponseEntity<Result> activeMeal() {

        try {
            schoolService.activeMealUpload();

            return new ResponseEntity<>(new Result<>(null, false), HttpStatus.OK);
        } catch (IllegalStateException e){
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found school")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), status);
        }
    }

    @PostMapping("/admin/school/deactive-meal")
    public ResponseEntity<Result> deactivateMeal() {

        try {
            schoolService.deactivateMealUpload();

            return new ResponseEntity<>(new Result<>(null, false), HttpStatus.OK);
        } catch (IllegalStateException e){
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().equals("not found school")) status = HttpStatus.NOT_FOUND;

            return new ResponseEntity<>(new Result<>(null, true), status);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), status);
        }
    }
}
