package com.pcs.daejeon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.config.security.auth.PrincipalDetails;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.MemberRepository;
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

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
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
    @Autowired
    MemberRepository memberRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    private Member getLoginMember() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return member.getMember();
    }

    private Map<String, Object> createMember() throws Exception {
        SignUpDto user = new SignUpDto(
                "01012341234",
                getLoginMember().getSchool().getId(),
                "testPassword",
                "testId" + (int) (Math.random() * 100)
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
    @DisplayName("자신의 정보 가져오기 성공")
    void memberInfo() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/member/info"))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("자신의 정보 가져오기 실패 - 미 로그인")
    void memberInfo401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/member/info"))
                .andExpect(status().isUnauthorized());
    }
}