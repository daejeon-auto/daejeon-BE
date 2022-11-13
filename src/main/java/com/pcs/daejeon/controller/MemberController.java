package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@CrossOrigin("*")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<Result> signUp(@RequestBody SignUpDto signUpDto) {

        try {
            Member save = memberService.saveMember(signUpDto);
            return new ResponseEntity<>(new Result(save.getId(), false), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result(e, true), HttpStatus.BAD_REQUEST);
        }
    }
}
