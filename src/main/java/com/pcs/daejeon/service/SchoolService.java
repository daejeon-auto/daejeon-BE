package com.pcs.daejeon.service;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.school.SchoolRegistDto;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SchoolService {

    private final Util util;
    private final SchoolRepository schoolRepository;

    public School regist(SchoolRegistDto schoolRegistDto) {
        School school = new School(schoolRegistDto.getName(),
                schoolRegistDto.getLocate(),
                schoolRegistDto.getInstaId(),
                schoolRegistDto.getInstaPwd());

        return schoolRepository.save(school);
    }

}
