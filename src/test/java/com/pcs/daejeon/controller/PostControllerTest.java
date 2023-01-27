package com.pcs.daejeon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Result;
import com.pcs.daejeon.dto.post.PostDto;
import com.pcs.daejeon.dto.post.PostListDto;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (int i = 0; i < 100; i++) {
            postRepository.save(
                    new Post("testPost"+i,
                            new School("부컴과",
                                    "부산",
                                    "인스타아이디",
                                    "인스타pwd")));
        }
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
            assertThat(postDto.getPostId()).isEqualTo(100-i++);
        }
    }

    @Test
    void writePost() {
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