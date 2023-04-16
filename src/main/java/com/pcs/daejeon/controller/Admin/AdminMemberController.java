package com.pcs.daejeon.controller.Admin;

import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.MemberListDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
// member를 관리하는 admin controller
public class AdminMemberController {

    private final MemberService memberService;
    private final Util util;

    // 유저 정보 가져오기 - 자신의 학교 한정
    @PostMapping("/admin/members")
    public ResponseEntity<Result<List<MemberListDto>>> getMembers(
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "onlyAdmin", required = false) boolean onlyAdmin) {

        try {
            if (!util.getLoginMember().getRole().equals(RoleTier.ROLE_TIER1))
                throw new IllegalStateException("unauthorization");
            List<Member> members = memberService.getMembers(memberId, onlyAdmin);
            List<MemberListDto> memberListDto = members.stream()
                    .map(o -> new MemberListDto(o.getId(), o.getMemberType()))
                    .toList();

            return new ResponseEntity<>(new Result<>(memberListDto, false), HttpStatus.OK);
        } catch (IllegalStateException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;

           if (e.getMessage().equals("unauthorization")) status = HttpStatus.UNAUTHORIZED;

            return new ResponseEntity<>(new Result<>( null, true), status);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>( null, true), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/member/set-role/{id}/{role}")
    public ResponseEntity<Result<String>> setRole(@PathVariable("id") Long memberId, @PathVariable("role") RoleTier tier) {

        try {
            Member member = memberService.setMemberRole(memberId, tier);

            log.info("[set-role] set role memberId = "+member.getId()+" changed role = "+member.getRole().toString()+" by adminId = "+util.getLoginMember().getId());
            return new ResponseEntity<>(new Result<>("success", false), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new Result<>(null, true), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("e = " + e);
            return new ResponseEntity<>(new Result<>("server error", true), HttpStatus.BAD_REQUEST);
        }
    }
}
