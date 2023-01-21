package com.pcs.daejeon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.type.AuthType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

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
    MemberController memberController;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void signUp() throws Exception {
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
        mvc.perform(MockMvcRequestBuilders
                        .post("/sign-up")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void getCodeList() {
    }

    @Test
    void acceptMember() {
    }

    @Test
    void rejectMember() {
    }

    @Test
    void memberInfo() {
    }
}