package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.MemberInfoDto;
import com.pcs.daejeon.dto.member.ReferCodeDto;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.ReferCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ReferCodeService referCodeService;
    private final Util util;

    @PostMapping("/sign-up")
    public ResponseEntity<Result<String>> signUp(@RequestBody @Valid SignUpDto signUpDto) {

        try {
            Member save = memberService.saveMember(signUpDto);

            if (save.getAuthType().equals(AuthType.DIRECT)) {
                for (int i = 0; i < 3; i++) {
                    referCodeService.generateCode(save);
                }
            }

            return new ResponseEntity<>(new Result<>(save.getId().toString(), false), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            if (Objects.equals(e.getMessage(), "student already sign up")) {
                return new ResponseEntity<>(new Result<>(e.getMessage(), true), HttpStatus.CONFLICT);
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

    @PostMapping("/code/list")
    public ResponseEntity<Result<List<ReferCodeDto>>> getCodeList() {
        try {
            List<ReferCode> referCodeList = referCodeService.getReferCodeList();
            List<ReferCodeDto> result = referCodeList.stream()
                    .map(o -> new ReferCodeDto(
                            o.getId(),
                            o.getCode(),
                            o.getUsedBy() != null ? o.getUsedBy().getName() : null,
                            o.getCreatedDate(),
                            o.isUsed()
                    )).toList();

            return new ResponseEntity<>(new Result<>(result, false), HttpStatus.OK);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/member/accept/{id}")
    public ResponseEntity<Result<String>> acceptMember(@PathVariable("id") Long id) {
        try {
            memberService.acceptMember(id);

            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e ) {
            if (Objects.equals(e.getMessage(), "member not found")) {
                return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
            }

            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/member/reject/{id}")
    public ResponseEntity<Result<String>> rejectMember(@PathVariable("id") Long id) {
        try {
            memberService.rejectMember(id);

            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e ) {
            if (Objects.equals(e.getMessage(), "member not found")) {
                return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
            }

            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/member/info")
    public ResponseEntity<Result<MemberInfoDto>> memberInfo() {

        try {
            Member loginMember = util.getLoginMember();

            MemberInfoDto memberInfoDto = new MemberInfoDto(
                    loginMember.getName(),
                    loginMember.getStudentNumber(),
                    loginMember.getBirthDay(),
                    loginMember.getPhoneNumber(),
                    loginMember.getAuthType()
            );

            return new ResponseEntity<>(new Result<>(memberInfoDto, false), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.BAD_REQUEST);
        }
    }
}
