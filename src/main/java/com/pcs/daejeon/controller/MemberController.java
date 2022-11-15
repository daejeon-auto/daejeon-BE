package com.pcs.daejeon.controller;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.service.MemberService;
import com.pcs.daejeon.service.ReferCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

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

    @PostMapping("/code/generate")
    public ResponseEntity<Result> generateCode() {
        try {
            String code = referCodeService.generateCode();

            return new ResponseEntity<>(new Result(code, false), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result("fail to generate code", true), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/code/list")
    public ResponseEntity<Result> getCodeList() {
        try {
            List<ReferCode> referCodeList = referCodeService.getReferCodeList();
            Stream<String> codeListDto = referCodeList.stream()
                    .map(o -> o.getCode());

            return new ResponseEntity<>(new Result(codeListDto, false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result(e.getMessage(), true), HttpStatus.BAD_REQUEST);
        }
    }
}
