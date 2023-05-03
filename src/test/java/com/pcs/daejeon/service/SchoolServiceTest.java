package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.school.MealDto;
import com.pcs.daejeon.dto.school.SchoolRegistDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@ActiveProfiles("test")
class SchoolServiceTest {

    @Autowired
    SchoolService schoolService;

    @Autowired
    Util util;

    @Test
    void findAllSchool() {
        // given
        List<School> allSchool = schoolService.findAllSchool();

        // when
        for (Object school : allSchool) {
            // then
            assertThat(school instanceof School).isTrue();
        }

    }

    @Test
    void findSchool() {

        // given
        Long schoolId = util.getLoginMember().getSchool().getId();

        // when
        School school = schoolService.findSchool(schoolId);

        // then
        assertThat(school.getId()).isEqualTo(schoolId);
    }

    @Test
    void getMealServiceInfo() throws IOException {

        // given
        Member loginMember = util.getLoginMember();
        School school = schoolService.findSchool(loginMember.getSchool().getId());

        // when
        MealDto mealServiceInfo = schoolService.getMealServiceInfo(school.getCode(), school.getLocationCode());

        // then
        assertThat(mealServiceInfo.getBreakfast().getClass()).isExactlyInstanceOf(String[].class);
        assertThat(mealServiceInfo.getLunch().getClass()).isExactlyInstanceOf(String[].class);
        assertThat(mealServiceInfo.getDinner().getClass()).isExactlyInstanceOf(String[].class);
    }

    @Test
    void getSchoolCodes() throws IOException {

        // given
        SchoolRegistDto schoolRegistDto = new SchoolRegistDto(
                "부산컴퓨터과학고등학교",
                "부산광역시");

        //when
        String[] schoolCodes = schoolService.getSchoolCodes(
                schoolRegistDto.getName(),
                schoolRegistDto.getLocate());

        //then
        assertThat(schoolCodes[0]).isEqualTo("7150337");
        assertThat(schoolCodes[1]).isEqualTo("C10");
    }
}