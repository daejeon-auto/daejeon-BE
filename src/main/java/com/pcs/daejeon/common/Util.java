package com.pcs.daejeon.common;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Util {

    private final PasswordEncoder pwdEncoder;
    private final SchoolRepository schoolRepository;

    public Member createMember(SignUpDto signUpDto) {
        Optional<School> school = schoolRepository.findById(signUpDto.getSchoolId());

        if (school.isEmpty()) {
            throw new IllegalStateException("school not found");
        }

        return new Member(
                signUpDto.getName(),
                signUpDto.getBirthDay(),
                signUpDto.getPhoneNumber(),
                signUpDto.getStudentNumber(),
                pwdEncoder.encode(signUpDto.getPassword()),
                signUpDto.getLoginId(),
                signUpDto.getAuthType(),
                school.get());
    }

    public Member getLoginMember() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return member.getMember();
    }
}
