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
    @DisplayName("회원가입 성공")
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
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .post("/sign-up")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // String result to object
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);

        Assertions.assertThat(map.get("hasError")).isEqualTo(false);
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