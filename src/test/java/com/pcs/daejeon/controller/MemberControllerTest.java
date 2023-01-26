package com.pcs.daejeon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.type.AuthType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockCustomUser
@ActiveProfiles("test")
@Transactional
@Rollback
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("회원가입 성공")
    void signUp() throws Exception {
        Map<String, Object> map = createMember();

        Assertions.assertThat(map.get("hasError")).isEqualTo(false);
    }

    @Test
    @DisplayName("자신의 코드 리스트 가져오기 성공")
    void getCodeList() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/code/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 승인 성공")
    void acceptMember() throws Exception {
        Map<String, Object> map = createMember();

        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/accept/"+map.get("data")))
                .andExpect(status().isAccepted())
                .andDo(print());
    }

    private Map<String, Object> createMember() throws Exception {
        SignUpDto user = new SignUpDto(
                "test1",
                "200000101",
                "01012341234",
                AuthType.DIRECT,
                "" + (int) (Math.random() * 100000),
                "testPassword",
                "testId" + (int) (Math.random() * 100),
                "부산컴퓨터과학고등학교",
                "부산",
                "인스타아이디",
                "인스타비밀번호"
        );
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .post("/sign-up")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        return map;
    }


    @Test
    @DisplayName("유저 승인 실패 - 유저 없음")
    void acceptMember404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/admin/member/accept/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유저 승인 실패 - 미 로그인")
    void acceptMember401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/accept/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockCustomUser(role = "ROLE_TIER0")
    @DisplayName("유저 승인 싫패 - 권한없음")
    void acceptMember403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/accept/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("유저 거절 성공")
    void rejectMember200() throws Exception {
        Map<String, Object> member = createMember();

        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/reject/"+member.get("data")))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("유저 거절 실패 - 유저 없음")
    void rejectMember404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/reject/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유저 거절 실패 - 미 로그인")
    void rejectMember401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/reject/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유저 거절 실패 - 권한 부족")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void rejectMember403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/member/reject/1"))
                .andExpect(status().isForbidden());
    }


    @Test
    void memberInfo() {
    }
}