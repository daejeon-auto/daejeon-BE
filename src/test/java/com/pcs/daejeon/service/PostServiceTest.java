package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@AutoConfigureMockMvc
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("글 작성 성공")
    void writePost200() {
        Long postId = postService.writePost("test글 작성");

        Optional<Post> post = postRepository.findById(postId);

        Assertions.assertFalse(post.isEmpty());
    }

    @Test
    @DisplayName("글 작성 실패 - 글자수 초과")
    void writePost400Len() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> postService.writePost(
                            "test글 작성------------------------" +
                                    "-------------------------------------" +
                                    "-----------------------------------------"));
    }

    @Test
    @DisplayName("글 작성 실패 - 욕설 포함")
    void writePost400BadWord() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.writePost("시발"));
    }

    @Test
    void deletePost() {
    }

    @Test
    void acceptPost() {
    }

    @Test
    void findPagedPost() {
    }

    @Test
    void findPagedPostByMemberId() {
    }

    @Test
    void findPagedRejectedPost() {
    }

    @Test
    void searchPost() {
    }

    @Test
    void findPostById() {
    }

    @Test
    void addLike() {
    }
}