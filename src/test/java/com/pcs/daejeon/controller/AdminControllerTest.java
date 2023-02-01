package com.pcs.daejeon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.dto.member.PendingMemberDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

    long examplePostId = 0;
    long exampleSchoolId = 0;
    Member exampleMember = null;

    private Member getLoginMember() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return member.getMember();
    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (int i = 0; i < 100; i++) {
            Post save = postRepository.save(
                    new Post("testPost" + i, getLoginMember().getSchool()));
            examplePostId = save.getId();
        }

        School school = schoolRepository.save(new School(
                "부산컴퓨터과학고",
                "부산",
                "인스타아이디",
                "인스타패스워드"
        ));

        exampleSchoolId = school.getId();

        Member member = memberRepository.save(new Member(
                "testMember",
                "000000",
                "01012341234",
                "00000",
                "password",
                "loginId",
                AuthType.DIRECT,
                school
        ));
        exampleMember = member;
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
    @DisplayName("대기 유저 가져오기 성공")
    void getPendingMembers() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .post("/admin/members/pending"))
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @DisplayName("대기 유저 가져오기 실패 - 권한 부족")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void getPendingMembers403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/members/pending"))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("대기 유저 가져오기 실패 - 미 로그인")
    void getPendingMembers401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/members/pending"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("대기 유저 승인 성공")
    void acceptPendingMember() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                            exampleSchoolId,
                            exampleMember.getBirthDay(),
                            exampleMember.getName(),
                            exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isAccepted());

        MemberType memberType = exampleMember.getMemberType();
        assertThat(memberType).isEqualTo(MemberType.ACCEPT);
    }
    @Test
    @DisplayName("대기 유저 승인 실패 - 권한 부족")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void acceptPendingMember403() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                            exampleSchoolId,
                            exampleMember.getBirthDay(),
                            exampleMember.getName(),
                            exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("대기 유저 승인 실패 - 미로그인")
    void acceptPendingMember401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                            exampleSchoolId,
                            exampleMember.getBirthDay(),
                            exampleMember.getName(),
                            exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("대기 유저 승인 실패 - 유저 없음")
    void acceptPendingMember404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                            exampleSchoolId,
                            "123123",
                            exampleMember.getName(),
                            exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("대기 유저 거절 성공")
    void rejectPendingMember() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                                exampleSchoolId,
                                exampleMember.getBirthDay(),
                                exampleMember.getName(),
                                exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isAccepted());
    }
    @Test
    @DisplayName("대기 유저 거절 실패 - 미로그인")
    void rejectPendingMember401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                                exampleSchoolId,
                                exampleMember.getBirthDay(),
                                exampleMember.getName(),
                                exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("대기 유저 거절 실패 - 권한 부족")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void rejectPendingMember403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/pending-member/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(new PendingMemberDto(
                                exampleSchoolId,
                                exampleMember.getBirthDay(),
                                exampleMember.getName(),
                                exampleMember.getStudentNumber()
                        ))
                ))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("대기 유저 거절 실패 - 유저 없음")
    void rejectPendingMember404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/pending-member/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(new PendingMemberDto(
                                        exampleSchoolId,
                                        "123123",
                                        exampleMember.getName(),
                                        exampleMember.getStudentNumber()
                                ))
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("개인 정보 가져오기 성공")
    void callPersonalInfo() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .post("/admin/personal-info/" + exampleMember.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Result<PersonalInfo> result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<Result<PersonalInfo>>() {});

        assertThat(result.getData().getName()).isEqualTo(exampleMember.getName());
    }
    @Test
    @DisplayName("개인 정보 가져오기 실패 - 미로그인")
    void callPersonalInfo401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/personal-info/" + exampleMember.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("개인 정보 가져오기 실패 - 권한 부족 1")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void callPersonalInfo403_1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/personal-info/" + exampleMember.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("개인 정보 가져오기 실패 - 권한 부족 2")
    @WithMockCustomUser(role = "ROLE_TIER1")
    void callPersonalInfo403_2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/personal-info/" + exampleMember.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("개인 정보 가져오기 실패 - 유저 없음")
    void callPersonalInfo404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/personal-info/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void setRole() {
    }

    @Test
    void rejectPostList() {
    }
}