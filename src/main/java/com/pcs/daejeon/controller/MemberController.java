package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.chkCode.ChkCodeDto;
import com.pcs.daejeon.dto.chkCode.PushCodeDto;
import com.pcs.daejeon.dto.member.MemberInfoDto;
import com.pcs.daejeon.dto.member.SignUpAdminDto;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.dto.report.AddReportBullyingDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.RefreshTokenService;
import com.pcs.daejeon.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final SchoolService schoolService;
    private final RefreshTokenService refreshTokenService;
    private final Util util;

    @PostMapping("/push-chk-code")
    public ResponseEntity<Result> pushChkCode(@RequestBody @Valid PushCodeDto pushCodeDto) {

        try {
            memberService.pushCheckCode(pushCodeDto.getPhoneNumber());
            return new ResponseEntity<>(new Result<>(null, false), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/chk-code")
    public ResponseEntity<Result> chkCode(@RequestBody @Valid ChkCodeDto chkCodeDto) {

        try {
            boolean isCheck = memberService.checkCode(chkCodeDto.getCode(), chkCodeDto.getPhoneNumber());

            return new ResponseEntity<>(new Result<>(null, !isCheck),
                    isCheck ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Result<String>> signUp(@RequestBody @Valid SignUpDto signUpDto) {

        try {
            Member save = memberService.saveMember(signUpDto);

            return new ResponseEntity<>(new Result<>(save.getId().toString(), false), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            if (Objects.equals(e.getMessage(), "student already sign up")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.CONFLICT);
            }
            if (Objects.equals(e.getMessage(), "school not found")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.NOT_FOUND);
            }

            if (Objects.equals(e.getMessage(), "unused refer code not found")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.NOT_FOUND);
            }

            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        } catch (MethodArgumentNotValidException e) {
            return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signup-admin")
    public ResponseEntity<Result> signUpAdmin(@RequestBody @Valid SignUpAdminDto signUpAdminDto) {

        try {
            String[] schoolCodes = schoolService.getSchoolCodes(
                    signUpAdminDto.getSchool().getName(),
                    signUpAdminDto.getSchool().getLocate());

            Member member = memberService.saveAdmin(signUpAdminDto.getMember(), signUpAdminDto.getSchool(), schoolCodes);

            MemberInfoDto memberInfo = new MemberInfoDto(
                    member.getPhoneNumber(),
                    member.getSchool().getName(),
                    member.getSchool().getLocate(),
                    member.getPunish());

            return new ResponseEntity<>(new Result<>(memberInfo, false), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            if (Objects.equals(e.getMessage(), "student already sign up")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.CONFLICT);
            }

            if (Objects.equals(e.getMessage(), "unused refer code not found")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.NOT_FOUND);
            }

            if (Objects.equals(e.getMessage(), "school already exist")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.CONFLICT);
            }

            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        } catch (MethodArgumentNotValidException e) {
            return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/member/accept/{id}")
    public ResponseEntity<Result<String>> acceptMember(@PathVariable("id") Long id) {
        try {
            memberService.acceptMember(id);

            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e ) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (Objects.equals(e.getMessage(), "member not found")) {
                status = HttpStatus.NOT_FOUND;
            }

            if (Objects.equals(e.getMessage(), "school is different")) {
                status = HttpStatus.FORBIDDEN;
            }

            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), status);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/member/info")
    public ResponseEntity<Result<MemberInfoDto>> memberInfo() {

        try {
            Member loginMember = memberService.findMember(util.getLoginMember().getId());
            List<Punish> activePunish = loginMember.getPunish().stream()
                    .map(val -> val.isValid() ? val : null).toList();

            MemberInfoDto memberInfoDto = new MemberInfoDto(
                    loginMember.getPhoneNumber(),
                    loginMember.getSchool().getName(),
                    loginMember.getSchool().getLocate(),
                    activePunish
                    );

            return new ResponseEntity<>(new Result<>(memberInfoDto, false), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Result> refreshAuth(HttpServletRequest request) {

        try {
            String refreshToken = refreshTokenService.createRefreshToken(request);

            return ResponseEntity.ok()
                    .header("X-Auth-Token", "Bearer " + refreshToken)
                    .body(new Result<>(null, false));
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/report-bullying")
    public ResponseEntity<Result> reportBullying(@RequestBody @Valid AddReportBullyingDto addReportBullyingDto) {

        try {
            memberService.reportBullying(addReportBullyingDto);
            return new ResponseEntity<>(new Result<>(null, false), HttpStatus.OK);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }
}
