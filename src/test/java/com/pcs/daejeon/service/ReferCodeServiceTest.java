package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.ReferCodeRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Rollback
class ReferCodeServiceTest {

    @Autowired
    ReferCodeService referCodeService;

    @Autowired
    ReferCodeRepository referCodeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Test
    @DisplayName("code생성 성공")
    void generateCode() {
        School school = new School("부컴과", "부산", "인스타아이디", "패스워드");
        schoolRepository.save(school);
        Member save = memberRepository.save(new Member(
                "test 계정",
                "123121",
                "01012341234",
                "10101",
                "password123",
                "GenerateCode",
                AuthType.DIRECT,
                school
        ));

        referCodeService.generateCode(save);

        List<ReferCode> allByCodeList = referCodeRepository.findAllByCodeList(save);
        assertThat(allByCodeList.size()).isEqualTo(1);
        for (ReferCode referCode : allByCodeList) {
            assertThat(referCode.isUsed()).isEqualTo(false);
            assertThat(referCode.getCreatedBy().getId()).isEqualTo(save.getId());
        }
    }

    @Test
    @DisplayName("code생성 실패 - 간접가입")
    void generateCode400() {
        School school = new School("부컴과", "부산", "인스타아이디", "패스워드");
        schoolRepository.save(school);
        Member save = memberRepository.save(new Member(
                "test 계정2",
                "123121",
                "01012341234",
                "11233",
                "password123",
                "GenerateCode1",
                AuthType.INDIRECT,
                school
        ));

        assertThrows(IllegalStateException.class,
                () -> referCodeService.generateCode(save),
                "this account is not signed up with direct");
    }

    @Test
    @DisplayName("코드 생성 실패 - 과잉 발급")
    void generateCodeToMany() {
        School school = new School("부컴과", "부산", "인스타아이디", "패스워드");
        schoolRepository.save(school);
        Member save = memberRepository.save(new Member(
                "test 계정2",
                "123121",
                "01012341234",
                "11233",
                "password123",
                "GenerateCode1",
                AuthType.DIRECT,
                school
        ));

        for (int i = 0; i < 4; i++) {
            referCodeService.generateCode(save);
        }

        assertThrows(IllegalStateException.class,
                () -> referCodeService.generateCode(save),
                "too many code");
    }

    @Test
    @WithMockCustomUser
    void getReferCodeList() {
    }
}