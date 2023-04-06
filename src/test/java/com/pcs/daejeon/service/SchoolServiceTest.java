package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@ActiveProfiles("test")
class SchoolServiceTest {

    @Autowired
    SchoolService schoolService;

    @Test
    void getSchoolInfo() throws IOException {
        String schoolCode = schoolService.getSchoolCode("부산컴퓨터과학고등학교", "부산광역시");
        Assertions.assertThat("7150337").isEqualTo(schoolCode);
    }
}