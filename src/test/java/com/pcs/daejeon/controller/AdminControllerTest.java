package com.pcs.daejeon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

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

    ObjectMapper mapper = new ObjectMapper();

    long examplePostId = 0;

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
    void acceptPendingMember() {
    }

    @Test
    void rejectPendingMember() {
    }

    @Test
    void callPersonalInfo() {
    }

    @Test
    void setRole() {
    }

    @Test
    void rejectPostList() {
    }
}