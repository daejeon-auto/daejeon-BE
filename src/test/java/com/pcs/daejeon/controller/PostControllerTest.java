package com.pcs.daejeon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.dto.post.PostDto;
import com.pcs.daejeon.dto.post.PostListDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.PostRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockCustomUser
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
class PostControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PostRepository postRepository;

    ObjectMapper mapper = new ObjectMapper();
    boolean isSetUp = false;

    @BeforeEach
    void setUp() {
        isSetUp = true;
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (int i = 0; i < 100; i++) {
            postRepository.save(
                    new Post("testPost"+i, getLoginMember().getSchool()));
        }
    }
    private Member getLoginMember() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return member.getMember();
    }
    @Test
    @DisplayName("포스트 가져오기 성공")
    void getPostPage() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        MvcResult posts = mvc.perform(MockMvcRequestBuilders
                        .post("/posts"))
                .andExpect(status().isOk())
                .andReturn();

        Result<PostListDto> map = mapper.readValue(posts.getResponse().getContentAsString(),
                new TypeReference<Result<PostListDto>>() {});

        assertThat(map.getData().getTotalPost()).isEqualTo(100);

        int i = 0;
        for (PostDto postDto : map.getData().getPostList()) {
            assertThat(postDto.getPostId()).isEqualTo(400-i++);
        }
    }

    @Test
    @DisplayName("포스트 작성 성공")
    void writePost() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/post/write")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"test글 작성\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포스트 작성 실패 - 미로그인")
    void writePost401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/post/write")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"test글 작성\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 작성 실패 - 욕설")
    void writePost400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/post/write")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"시발 좆같네 ----------------------------\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("포스트 작성 실패 - 글자수 초과101")
    void writePost400_over() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .post("/post/write")
                        .content("{\"description\": \"-----------------------------------------------------------------------------------------------------\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

//        String contentAsString = mapper.writeValueAsString(mvcResult.getResponse().getContentAsString());
//        Result result = mapper.readValue(contentAsString, Result.class);
//
//        System.out.println(result.getData());
    }

    @Test
    @DisplayName("포스트 작성 실패 - 글자수 부족")
    void writePost400_under() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/post/write")
                .content("{\"description\": \"\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reportPost() {
    }

    @Test
    void acceptPost() {
    }

    @Test
    void rejectedPost() {
    }

    @Test
    void addLiked() {
    }

    @Test
    void rejectPostList() {
    }

    @Test
    void getWrotePosts() {
    }
}