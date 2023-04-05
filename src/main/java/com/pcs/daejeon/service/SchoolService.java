package com.pcs.daejeon.service;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SchoolService {

    private final Util util;
    private final SchoolRepository schoolRepository;

    public List<School> findAllSchool() {
        return schoolRepository.findAll();
    }
    public School findSchool(Long schoolId) {
        Member loginMember = util.getLoginMember();
        if (schoolId == 0 && loginMember != null) {
            schoolId = loginMember.getSchool().getId();
        }

        Optional<School> school = schoolRepository.findById(schoolId);

        if (school.isEmpty()) throw new IllegalStateException("school not found");
        return school.get();
    }
}
