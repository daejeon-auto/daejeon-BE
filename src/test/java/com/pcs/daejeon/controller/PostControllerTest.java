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
    long examplePostId = 0;

    @BeforeEach
    void setUp() {
        isSetUp = true;
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (int i = 0; i < 100; i++) {
            Post save = postRepository.save(
                    new Post("testPost" + i, getLoginMember().getSchool()));
            examplePostId = save.getId();
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
            assertThat(postDto.getPostId()).isEqualTo(examplePostId-i++);
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
    @DisplayName("신고 성공")
    void reportPost() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/post/report/"+examplePostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\": \"이거 문제 있습니다\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("신고 실패 - 401 미로그인")
    void reportPost401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/post/report/"+examplePostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\": \"이거 문제 있습니다\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("신고 실패 - 404 포스트 없음")
    void reportPost404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/post/report/"+0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\": \"이거 문제 있습니다\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("신고 실패 - 400 이유 없음")
    void reportPost400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/post/report/"+0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\": \"\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("포스트 승인 성공")
    void acceptPost() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/accept/"+examplePostId))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("포스트 승인 실패 - 포스트 없음")
    void acceptPost404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/accept/"+0))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("포스트 승인 실패 - 미로그인")
    void acceptPost401() throws Exception {
        mvc.perform(logout()).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/accept/"+examplePostId))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 승인 실패 - 권한 없음")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void acceptPost403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/accept/"+examplePostId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("포스트 차단 성공")
    void rejectedPost() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/reject/"+examplePostId))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("포스트 차단 실패 - 미로그인")
    void rejectedPost401() throws Exception {
        mvc.perform(logout());
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/reject/"+examplePostId))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 차단 실패 - 권한 없음")
    @WithMockCustomUser(role = "ROLE_TIER0")
    void rejectedPost403() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/reject/"+examplePostId))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("포스트 차단 실패 - 포스트 없음")
    void rejectedPost404() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/admin/post/reject/"+0))
                .andExpect(status().isNotFound());
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