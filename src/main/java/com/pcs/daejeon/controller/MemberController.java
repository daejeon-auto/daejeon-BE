package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.account.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.ReferCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ReferCodeService referCodeService;

    @PostMapping("/sign-up")
    public ResponseEntity<Result> signUp(@RequestBody @Valid SignUpDto signUpDto) {

        try {
            Member save = memberService.saveMember(signUpDto);
            return new ResponseEntity<>(new Result(save.getId(), false), HttpStatus.CREATED);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result("error", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/code/generate")
    public ResponseEntity<Result> generateCode() {
        try {
            String code = referCodeService.generateCode();

            return new ResponseEntity<>(new Result(code, false), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result("fail to generate code", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/code/list")
    public ResponseEntity<Result> getCodeList() {
        try {
            List<ReferCode> referCodeList = referCodeService.getReferCodeList();
            Stream<String> codeListDto = referCodeList.stream()
                    .map(ReferCode::getCode);

            return new ResponseEntity<>(new Result(codeListDto, false), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("e = " + e);
            return new ResponseEntity<>(new Result(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/member/accept/{id}")
    public ResponseEntity<Result> acceptMember(@PathVariable("id") Long id) {
        try {
            memberService.acceptMember(id);

            return new ResponseEntity<>(new Result("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e ) {
            if (Objects.equals(e.getMessage(), "member not found")) {
                return new ResponseEntity<>(new Result("not found member", true), HttpStatus.NOT_FOUND);
            }

            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/member/reject/{id}")
    public ResponseEntity<Result> rejectMember(@PathVariable("id") Long id) {
        try {
            memberService.rejectMember(id);

            return new ResponseEntity<>(new Result("success", false), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e ) {
            if (Objects.equals(e.getMessage(), "member not found")) {
                return new ResponseEntity<>(new Result("not found member", true), HttpStatus.NOT_FOUND);
            }

            log.debug("e = " + e);
            return new ResponseEntity<>(new Result( "server error", true), HttpStatus.BAD_REQUEST);
        }
    }
}
