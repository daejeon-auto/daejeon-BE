package com.pcs.daejeon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockCustomUser
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
class AdminControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    MemberRepository memberRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Util util;

    long examplePostId = 0;
    long exampleSchoolId = 0;
    Member exampleMember = null;
    Member sameSchoolExampleMember = null;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        School school = util.getLoginMember().getSchool();
        exampleSchoolId = school.getId();

        for (int i = 0; i < 100; i++) {
            Post save = postRepository.save(
                    new Post("testPost" + i, school));
            examplePostId = save.getId();
        }

        Member member = memberRepository.save(new Member(
                "01012341234",
                "password",
                "loginId",
                school
        ));
        exampleMember = member;

        sameSchoolExampleMember = memberRepository.save(new Member(
                "01012341234",
                "password",
                "loginId",
                school
        ));
    }

    @Test
    @DisplayName("신고된 게시글 리스트 가져오기")
    void getReportList() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/reports/"+examplePostId))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("신고된 게시글 리스트 가져오기 실패 - 포스트 없음")
    void getReportList404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/reports/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("신고된 게시글 리스트 가져오기 실패 - 권한 없음")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void getReportList403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/reports/"+examplePostId))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("신고된 게시글 리스트 가져오기 실패 - 미로그인")
    void getReportList401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/reports/"+examplePostId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유저 리스트 가져오기 성공")
    void getMembers() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/members"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 리스트 가져오기 실패 - 권한 부족")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void getMembers403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/members"))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("유저 리스트 가져오기 실패 - 미로그인")
    void getMembers401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/members"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("권한 수정 성공")
    void setRole() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/member/set-role/" + sameSchoolExampleMember.getId() + "/ROLE_TIER1"))
                .andExpect(status().isOk());

        assertThat(sameSchoolExampleMember.getRole()).isEqualTo(RoleTier.ROLE_TIER1);
    }
    @Test
    @DisplayName("권한 수정 실패 - 권한 없음 1")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void setRole403_1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/member/set-role/" + exampleMember.getId() + "/ROLE_TIER1"))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("권한 수정 실패 - 권한 없음 2")
    @WithMockCustomUser(role = "ROLE_TIER1")
    void setRole403_2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/member/set-role/" + exampleMember.getId() + "/ROLE_TIER1"))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("권한 수정 실패 - 미로그인")
    void setRole401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/member/set-role/" + exampleMember.getId() + "/ROLE_TIER1"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("권한 수정 실패 - 유저 없음")
    void setRole404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/member/set-role/0/ROLE_TIER1"))
                .andExpect(status().isNotFound());
    }

//    @Test
//    @DisplayName("")
//    void rejectPostList() {
//    }
}