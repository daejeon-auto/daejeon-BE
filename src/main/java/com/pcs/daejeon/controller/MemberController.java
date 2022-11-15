package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.member.Member;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.ReferCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@CrossOrigin("*")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ReferCodeService referCodeService;

    @PostMapping("/sign-up")
    public ResponseEntity<Result> signUp(@RequestBody @Valid SignUpDto signUpDto) {

        try {
            Member save = memberService.saveMember(signUpDto);
            return new ResponseEntity<>(new Result(save.getId(), false), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result(e, true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generate-code")
    public ResponseEntity<Result> generateCode() {
        try {
            String code = referCodeService.generateCode();

            return new ResponseEntity<>(new Result(code, false), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result("fail to generate code", false), HttpStatus.BAD_REQUEST);
        }
    }
}
